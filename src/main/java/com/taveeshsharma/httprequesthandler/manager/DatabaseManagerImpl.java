package com.taveeshsharma.httprequesthandler.manager;

import com.bugbusters.orchastrator.Measurement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taveeshsharma.httprequesthandler.Constants;
import com.taveeshsharma.httprequesthandler.dto.ScheduleRequest;
import com.taveeshsharma.httprequesthandler.repository.ScheduleRequestRepository;
import org.influxdb.dto.Point;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseManagerImpl implements DatabaseManager{
    @Autowired
    private ScheduleRequestRepository scheduleRequestRepository;

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    @Override
    public void insertScheduledJob(ScheduleRequest request) {
        if (request.getRequestType().equals(Constants.RequestType.SCHEDULE_MEASUREMENT.toString())) {
            scheduleRequestRepository.insert(request);
        }
    }
}
