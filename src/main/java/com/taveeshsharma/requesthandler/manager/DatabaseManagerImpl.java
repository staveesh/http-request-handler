package com.taveeshsharma.requesthandler.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import com.taveeshsharma.requesthandler.dto.documents.ScheduleRequest;
import com.taveeshsharma.requesthandler.measurements.*;
import com.taveeshsharma.requesthandler.repository.PersonalDataRepository;
import com.taveeshsharma.requesthandler.repository.ScheduleRequestRepository;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DatabaseManagerImpl implements DatabaseManager{

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagerImpl.class);

    @Autowired
    private ScheduleRequestRepository scheduleRequestRepository;

    @Autowired
    private PersonalDataRepository personalDataRepository;

    @Autowired
    private InfluxDBTemplate<Point> influxDBpointTemplate;

    @Value("${spring.influxdb.database}")
    private String DB_NAME;

    @Value("${spring.influxdb.retention-policy}")
    private String RP_NAME;

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
        Gson gson = new GsonBuilder().create();
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

    private Point createTCPPoint(JSONObject jsonObject){
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");
        TCPMeasurement tcpMeasurement = (TCPMeasurement) buildMeasurements(jsonObject, TCPMeasurement.class);

        tcpMeasurement.setSpeedValues(measurementValues.getString("tcp_speed_results"));
        tcpMeasurement.setDataLimitExceeded(Boolean.parseBoolean(measurementValues.getString("data_limit_exceeded")));
        double duration = Double.parseDouble(measurementValues.getString("duration"));
        tcpMeasurement.setMeasurementDuration(Precision.round(duration, 2));
        return Point.measurementByPOJO(TCPMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(tcpMeasurement)
                .build();
    }

    private Point createPingPoint(JSONObject jsonObject) {
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");

        PingMeasurement pingMeasurement = (PingMeasurement) buildMeasurements(jsonObject, PingMeasurement.class);
        double mean, max, std;

        mean = Double.parseDouble(measurementValues.getString("mean_rtt_ms"));
        max = Double.parseDouble(measurementValues.getString("max_rtt_ms"));
        std = Double.parseDouble(measurementValues.getString("stddev_rtt_ms"));

        pingMeasurement.setTargetIpAddress(measurementValues.getString("target_ip"));
        pingMeasurement.setPingMethod(measurementValues.getString("ping_method"));
        pingMeasurement.setMeanRttMS(Precision.round(mean, 2));
        pingMeasurement.setMaxRttMs(Precision.round(max, 2));
        pingMeasurement.setStddevRttMs(Precision.round(std, 2));
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
        if(statusCode >= 300)
            return null;
        httpMeasurement.setHttpResultCode(statusCode);
        double duration = Double.parseDouble(measurementValues.getString("time_ms"));
        httpMeasurement.setTimeTakenMs(Precision.round(duration, 2));

        return Point.measurementByPOJO(HTTPMeasurement.class)
                .time(time, TimeUnit.MICROSECONDS)
                .addFieldsFromPOJO(httpMeasurement)
                .build();
    }

    private Point createTraceRTPoint(JSONObject jsonObject) {
        // TODO: Parse the values and alter the jsonObject according to measurement format
        JSONObject measurementValues = jsonObject.getJSONObject("values");
        long time = jsonObject.getLong("timestamp");

        TracerouteMeasurement tracerouteMeasurement = (TracerouteMeasurement) buildMeasurements(jsonObject, TracerouteMeasurement.class);

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
            measurements.setExperiment(object.getBoolean("isExperiment"));
            measurements.setTarget(getTargetKey(object.getJSONObject("parameters"), object.getString("type")));
            if (measurements.getIsExperiment())
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
            data.setUserName(ApiUtils.hashUserName(data.getUserName()));
            logger.info("Writing personal data : "+data);
            personalDataRepository.save(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<PersonalData> readPersonalData(String email) {
        String userName = ApiUtils.hashUserName(email);
        logger.info("Acquiring usage stats for userName : "+userName);
        List<PersonalData> networkUsage = personalDataRepository.getNetworkUsage(userName, new Date(0), new Date());
        return networkUsage;
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
}
