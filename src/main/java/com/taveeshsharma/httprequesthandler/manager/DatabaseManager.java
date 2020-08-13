package com.taveeshsharma.httprequesthandler.manager;

import com.taveeshsharma.httprequesthandler.dto.documents.PersonalData;
import com.taveeshsharma.httprequesthandler.dto.documents.ScheduleRequest;
import com.taveeshsharma.httprequesthandler.measurements.Measurements;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DatabaseManager {
    public void insertScheduledJob(ScheduleRequest request);

    public List<? extends Measurements> getMeasurement(String id, String type);

    public List<ScheduleRequest> getScheduledJobs(String type);

    public void writeValues(JSONObject jsonObject);

    public void writePersonalData(PersonalData data);

    public List<PersonalData> readPersonalData(String email);
}
