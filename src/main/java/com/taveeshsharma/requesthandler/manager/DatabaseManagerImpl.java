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

    @Autowired
    private VpnServerRepository vpnServerRepository;

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
            case Constants.HTTP_TYPE:
                return resultMapper.toPOJO(queryResult, HTTPMeasurement.class);
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
            case Constants.HTTP_TYPE:
                p = createHttpPoint(jsonObject);
                break;
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

    private Point createHttpPoint(JSONObject jsonObject) {
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");
        JSONObject values = jsonObject.getJSONObject("values");
        HTTPMeasurement httpMeasurement = (HTTPMeasurement) buildMeasurements(jsonObject, HTTPMeasurement.class);
        httpMeasurement.setDnsTime(Double.parseDouble(values.getString("dnsTime")));
        httpMeasurement.setSslTime(Double.parseDouble(values.getString("sslTime")));
        httpMeasurement.setTcpTime(Double.parseDouble(values.getString("tcpTime")));
        httpMeasurement.setPageLoadTime(Double.parseDouble(values.getString("pageLoadTime")));
        httpMeasurement.setRttPage(Double.parseDouble(values.getString("rttPage")));
        httpMeasurement.setRttResolver(Double.parseDouble(values.getString("rttResolver")));
        return Point.measurementByPOJO(HTTPMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(httpMeasurement)
                .build();
    }

    private Measurements buildMeasurements(JSONObject object, Class<? extends Measurements> T){
        try {
            Measurements measurements = T.newInstance();
            String user = object.getString("accountName");
            measurements.setDeviceId(object.getString("deviceId"));
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

    @Override
    public void updateVpnServers(List<VpnServer> servers) {
        vpnServerRepository.saveAll(servers);
    }

    @Override
    public VpnServer getBestVpnServer() {
        return vpnServerRepository.sortByPingAndSpeed();
    }
}