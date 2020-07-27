package com.taveeshsharma.httprequesthandler.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taveeshsharma.httprequesthandler.Constants;
import com.taveeshsharma.httprequesthandler.dto.ScheduleRequest;
import com.taveeshsharma.httprequesthandler.measurements.*;
import com.taveeshsharma.httprequesthandler.repository.ScheduleRequestRepository;
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

import java.util.List;

@Component
public class DatabaseManagerImpl implements DatabaseManager{

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagerImpl.class);

    @Autowired
    private ScheduleRequestRepository scheduleRequestRepository;

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    @Value("${spring.influxdb.database}")
    private String DB_NAME;

    @Override
    public void insertScheduledJob(ScheduleRequest request) {
        if (request.getRequestType().equals(Constants.RequestType.SCHEDULE_MEASUREMENT.toString())) {
            scheduleRequestRepository.insert(request);
        }
    }

    @Override
    public JSONObject getMeasurement(String id, String type) {
        QueryResult queryResult;
        if(id == null || id.isEmpty())
            queryResult = influxDBTemplate.query(new Query(
                    String.format("SELECT * FROM %s", type),
                    DB_NAME
            ));
        else
            queryResult = influxDBTemplate.query(
                    new Query(
                            String.format("SELECT * FROM %s WHERE taskKey = \'%s\'", type, id),
                            DB_NAME
                    ));
        Gson gson = new GsonBuilder().create();
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        switch (type.toUpperCase()) {
            case Constants.TCP_TYPE:
                return new JSONObject(gson.toJson(resultMapper.toPOJO(queryResult, TCPMeasurement.class)));
            case Constants.PING_TYPE:
                return new JSONObject(gson.toJson(resultMapper.toPOJO(queryResult, PingMeasurement.class)));
            case Constants.DNS_TYPE:
                return new JSONObject(gson.toJson(resultMapper.toPOJO(queryResult, DNSLookupMeasurement.class)));
            case Constants.HTTP_TYPE:
                return new JSONObject(gson.toJson(resultMapper.toPOJO(queryResult, HTTPMeasurement.class)));
            case Constants.TRACERT_TYPE:
                return new JSONObject(gson.toJson(resultMapper.toPOJO(queryResult, TracerouteMeasurement.class)));
            default:
                return null;
        }
    }

    @Override
    public List<ScheduleRequest> getScheduledJobs(String type) {
        return scheduleRequestRepository.getScheduledJobsFromType(type);
    }
}
