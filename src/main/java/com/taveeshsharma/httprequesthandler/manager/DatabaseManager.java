package com.taveeshsharma.httprequesthandler.manager;

import com.taveeshsharma.httprequesthandler.dto.ScheduleRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DatabaseManager {
    public void insertScheduledJob(ScheduleRequest request);

    public JSONObject getMeasurement(String id, String type);

    public List<ScheduleRequest> getScheduledJobs(String type);
}
