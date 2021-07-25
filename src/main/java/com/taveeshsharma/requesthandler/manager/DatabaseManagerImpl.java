package com.taveeshsharma.requesthandler.manager;

import com.taveeshsharma.requesthandler.dto.documents.*;
import com.taveeshsharma.requesthandler.repository.*;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import com.taveeshsharma.requesthandler.measurements.*;
import org.apache.commons.math3.util.Precision;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DatabaseManagerImpl implements DatabaseManager{

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagerImpl.class);

    @Autowired
    private ScheduleRequestRepository scheduleRequestRepository;

    @Autowired
    private PersonalDataRepository personalDataRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMetricsRepository jobMetricsRepository;

    @Autowired
    private InfluxDBTemplate<Point> influxDBpointTemplate;

    @Value("${spring.influxdb.database}")
    private String DB_NAME;

    @Value("${spring.influxdb.retention-policy}")
    private String RP_NAME;

    @Autowired
    private CiphersRepository ciphersRepository;

    @Autowired
    private FiltersRepository filtersRepository;

    @Override
    public void insertScheduledJob(ScheduleRequest request) {
        if (request.getRequestType().equals(Constants.RequestType.SCHEDULE_MEASUREMENT.toString())) {
            scheduleRequestRepository.insert(request);
        }
    }

    @Override
    public List<? extends Measurements> getMeasurement(String id, String type) {
        QueryResult queryResult;
        if(id == null || id.isEmpty())
            queryResult = influxDBpointTemplate.query(new Query(
                    String.format("SELECT * FROM %s", type),
                    DB_NAME
            ));
        else
            queryResult = influxDBpointTemplate.query(
                    new Query(
                            String.format("SELECT * FROM %s WHERE taskKey = \'%s\'", type, id),
                            DB_NAME
                    ));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        switch (type.toLowerCase()) {
            case Constants.TCP_TYPE:
                return resultMapper.toPOJO(queryResult, TCPMeasurement.class);
            case Constants.PING_TYPE:
                return resultMapper.toPOJO(queryResult, PingMeasurement.class);
            case Constants.DNS_TYPE:
                return resultMapper.toPOJO(queryResult, DNSLookupMeasurement.class);
            case Constants.HTTP_TYPE:
                return resultMapper.toPOJO(queryResult, HTTPMeasurement.class);
            case Constants.TRACERT_TYPE:
                return resultMapper.toPOJO(queryResult, TracerouteMeasurement.class);
            default:
                return null;
        }
    }

    @Override
    public List<ScheduleRequest> getScheduledJobs(String type) {
        return scheduleRequestRepository.getScheduledJobsFromType(type);
    }

    @Override
    public void writeValues(JSONObject jsonObject) {
        String type = (String) jsonObject.get("type");
        Point p;
        switch (type) {
            case Constants.TCP_TYPE:
                p = createTCPPoint(jsonObject);
                break;
            case Constants.PING_TYPE:
                p = createPingPoint(jsonObject);
                break;
            case Constants.DNS_TYPE:
                p = createDNSPoint(jsonObject);
                break;
            case Constants.HTTP_TYPE:
                p = createHttpPoint(jsonObject);
                break;
            case Constants.TRACERT_TYPE:
                try {
                    logger.info("Traceroute : "+jsonObject);
                    p = createTraceRTPoint(jsonObject);
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            default:
                p = null;
                break;
        }
        if(p != null)
            influxDBpointTemplate.write(p);
    }

    // Parses a string of form "[val1, val2, val3, ...]" and returns the values in a list
    private List<Double> parseTcpResults(String resultList){
        String replaced = resultList.replaceAll("^\\[|]$", "");
        return Arrays.stream(replaced.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    private Point createTCPPoint(JSONObject jsonObject){
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");
        TCPMeasurement tcpMeasurement = (TCPMeasurement) buildMeasurements(jsonObject, TCPMeasurement.class);

        tcpMeasurement.setSpeedValues(measurementValues.getString("tcp_speed_results"));
        tcpMeasurement.setDataLimitExceeded(Boolean.parseBoolean(measurementValues.getString("data_limit_exceeded")));
        double duration = Double.parseDouble(measurementValues.getString("duration"));
        tcpMeasurement.setMeasurementDuration(Precision.round(duration, 2));
        List<Double> values = parseTcpResults(tcpMeasurement.getSpeedValues());
        double mean = ApiUtils.mean(values);
        tcpMeasurement.setMeanSpeed(mean);
        tcpMeasurement.setMedianSpeed(ApiUtils.median(values));
        tcpMeasurement.setStdDevSpeed(ApiUtils.stddev(values, mean));
        tcpMeasurement.setMaxSpeed(ApiUtils.max(values));
        return Point.measurementByPOJO(TCPMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(tcpMeasurement)
                .build();
    }

    private Point createPingPoint(JSONObject jsonObject) {
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");

        PingMeasurement pingMeasurement = (PingMeasurement) buildMeasurements(jsonObject, PingMeasurement.class);
        double mean, max, std, packetLoss;

        mean = Double.parseDouble(measurementValues.getString("mean_rtt_ms"));
        max = Double.parseDouble(measurementValues.getString("max_rtt_ms"));
        std = Double.parseDouble(measurementValues.getString("stddev_rtt_ms"));
        packetLoss = Double.parseDouble(measurementValues.getString("packet_loss"));

        pingMeasurement.setTargetIpAddress(measurementValues.getString("target_ip"));
        pingMeasurement.setPingMethod(measurementValues.getString("ping_method"));
        pingMeasurement.setMeanRttMS(Precision.round(mean, 2));
        pingMeasurement.setMaxRttMs(Precision.round(max, 2));
        pingMeasurement.setStddevRttMs(Precision.round(std, 2));
        pingMeasurement.setPacketLoss(Precision.round(packetLoss, 2));
        pingMeasurement.setTimeMs(Long.parseLong(measurementValues.getString("time_ms")));
        return Point.measurementByPOJO(PingMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(pingMeasurement)
                .build();
    }

    private Point createDNSPoint(JSONObject jsonObject) {
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");

        DNSLookupMeasurement dnsLookupMeasurement = (DNSLookupMeasurement) buildMeasurements(jsonObject, DNSLookupMeasurement.class);

        dnsLookupMeasurement.setHostAddress(measurementValues.getString("address"));
        dnsLookupMeasurement.setHostName(measurementValues.getString("realHostname"));
        dnsLookupMeasurement.setTimeTaken(measurementValues.getDouble("timeMs"));

        return Point.measurementByPOJO(DNSLookupMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(dnsLookupMeasurement)
                .build();
    }

    private Point createHttpPoint(JSONObject jsonObject) {
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");

        HTTPMeasurement httpMeasurement = (HTTPMeasurement) buildMeasurements(jsonObject, HTTPMeasurement.class);
        int statusCode = Integer.parseInt(measurementValues.getString("code"));
        httpMeasurement.setHttpResultCode(statusCode);
        double duration;
        if(statusCode < 300) {
            duration = Double.parseDouble(measurementValues.getString("time_ms"));
            httpMeasurement.setTimeTakenMs(Precision.round(duration, 2));
        }
        return Point.measurementByPOJO(HTTPMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(httpMeasurement)
                .build();
    }

    private Point createTraceRTPoint(JSONObject jsonObject) {
        TracerouteMeasurement tracerouteMeasurement = (TracerouteMeasurement) buildMeasurements(jsonObject, TracerouteMeasurement.class);
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        Iterator<String> keys = measurementValues.keys();
        List<String> listOfHopsIPAddress = new ArrayList<>();
        List<String> listOfRTTs = new ArrayList<>();
        while(keys.hasNext()){
            String key = keys.next();;
            if(key.equalsIgnoreCase("num_hops"))
                tracerouteMeasurement.setNumberOfHops(Integer.parseInt(measurementValues.getString(key)));
            else if(key.equalsIgnoreCase("time_ms"))
                tracerouteMeasurement.setTimeMs(Long.parseLong(measurementValues.getString(key)));
            else{
                if(key.endsWith("rtt_ms"))
                    listOfRTTs.add(measurementValues.getString(key));
                else if(key.endsWith("addr_1"))
                    listOfHopsIPAddress.add(measurementValues.getString(key));
            }
        }
        tracerouteMeasurement.setListOfHopsIPAddress(String.join(",", listOfHopsIPAddress));
        tracerouteMeasurement.setListOfRTTs(String.join(",", listOfRTTs));
        long time = jsonObject.getLong("timestamp");
        return Point.measurementByPOJO(TracerouteMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(tracerouteMeasurement)
                .build();
    }

    private Measurements buildMeasurements(JSONObject object, Class<? extends Measurements> T){
        try {
            Measurements measurements = T.newInstance();
            String user = object.getString("accountName");
            measurements.setUserName(ApiUtils.hashUserName(user));
            measurements.setTarget(getTargetKey(object.getJSONObject("parameters"), object.getString("type")));
            if(object.has("taskKey"))
                measurements.setTaskKey(object.getString("taskKey"));
            else
                measurements.setTaskKey("N.A");
            return measurements;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTargetKey(JSONObject object, String type) {
        switch (type) {
            case Constants.TCP_TYPE:
            case Constants.TRACERT_TYPE:
            case Constants.DNS_TYPE:
            case Constants.PING_TYPE:
                return object.getString("target");
            case Constants.HTTP_TYPE:
                return object.getString("url");
            default:
                return "";
        }
    }

    @Override
    public void writePersonalData(PersonalData data) {
        try {
            logger.info("Writing personal data : "+data);
            personalDataRepository.save(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public List<Job> getCurrentlyActiveJobs(Date currentTime) {
        return jobRepository.getCurrentlyActiveJobs(currentTime);
    }

    @Override
    public void writePcapData(List<PcapMeasurements> pcapData) {
        BatchPoints batchPoints = BatchPoints
                .database(DB_NAME)
                .tag("async", "true")
                .retentionPolicy(RP_NAME)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        for (PcapMeasurements p : pcapData) {
            batchPoints.point(Point.measurementByPOJO(PcapMeasurements.class)
                    .time(p.getTime().toEpochMilli(), TimeUnit.MICROSECONDS)
                    .build());
        }
        influxDBpointTemplate.write(batchPoints.getPoints());
    }

    @Override
    public void upsertJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public void writeAccessPointInfo(JSONObject accessPoint) {
        long time = accessPoint.getLong("timestamp");
        AccessPointMeasurement accessPointMeasurement = new AccessPointMeasurement();
        accessPointMeasurement.setDeviceId(accessPoint.getString("deviceId"));
        accessPointMeasurement.setBSSID(accessPoint.getString("BSSID"));
        accessPointMeasurement.setSSID(accessPoint.getString("SSID"));
        accessPointMeasurement.setFrequency(accessPoint.getInt("frequency"));
        accessPointMeasurement.setIpAddress(accessPoint.getString("ipAddress"));
        accessPointMeasurement.setLinkSpeed(accessPoint.getInt("linkSpeed"));
        accessPointMeasurement.setMacAddress(accessPoint.getString("macAddress"));
        accessPointMeasurement.setRssi(accessPoint.getInt("rssi"));
        Point point = Point.measurementByPOJO(AccessPointMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(accessPointMeasurement)
                .build();
        influxDBpointTemplate.write(point);
    }

    @Override
    public void writeMobileDeviceInfo(JSONObject mobileDevice) {
        long time = mobileDevice.getLong("timestamp");
        MobileDeviceMeasurement mobile = new MobileDeviceMeasurement();
        mobile.setDeviceId(mobileDevice.getString("deviceId"));
        mobile.setLatitude(mobileDevice.getDouble("latitude"));
        mobile.setLongitude(mobileDevice.getDouble("longitude"));
        mobile.setLocationType(mobileDevice.getString("locationType"));
        mobile.setNetworkType(mobileDevice.getString("networkType"));
        mobile.setBattery(mobileDevice.getInt("battery"));
        mobile.setTemperature(mobileDevice.getInt("temperature"));
        Point point = Point.measurementByPOJO(MobileDeviceMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(mobile)
                .build();
        influxDBpointTemplate.write(point);
    }

    @Override
    public List<MobileDeviceMeasurement> getAvailableDevices() {
        // Gets a list of all devices that have checked-in during the last 10 minutes
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        long tenMinutesBack = cal.getTime().getTime()*1000*1000;
        QueryResult queryResult = influxDBpointTemplate.query(new Query(
                String.format("SELECT * FROM %s WHERE time > %s",
                        Constants.MOBILE_DEVICE_TYPE, tenMinutesBack),
                DB_NAME
        ));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, MobileDeviceMeasurement.class);
    }

    @Override
    public List<AccessPointMeasurement> getAllAccessPoints(String deviceId) {
        // Gets a list of all access points that the node was connected to during the past 1 hour
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        long oneHourBack = cal.getTime().getTime()*1000*1000;
        QueryResult queryResult = influxDBpointTemplate.query(new Query(
                String.format("SELECT * FROM %s WHERE time > %s AND device_id = \'%s\'",
                        Constants.ACCESS_POINT_TYPE, oneHourBack, deviceId),
                DB_NAME
        ));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, AccessPointMeasurement.class);
    }

    @Override
    public String findLastSummaryCheckinTime(String deviceId) {
        Date lastCheckin = personalDataRepository.findLastSummaryCheckIn(deviceId);
        if (lastCheckin == null)
            return "";
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(lastCheckin);
    }

    @Override
    public JobMetrics findMetricsById(String id) {
        Optional<JobMetrics> metrics = jobMetricsRepository.findById(id);
        return metrics.orElse(null);
    }

    @Override
    public void upsertJobMetrics(JobMetrics metrics) {
        jobMetricsRepository.save(metrics);
    }

    @Override
    public Cipher findBestCipherForLevel(String level) {
        return ciphersRepository.findBestCipherByLevel(level);
    }

    @Override
    public Filter findBestFilter(String networkType, String level){
        return filtersRepository.findBestFilter(networkType, level);
    }
}