package com.taveeshsharma.requesthandler.manager;

import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import com.taveeshsharma.requesthandler.dto.documents.ScheduleRequest;
import com.taveeshsharma.requesthandler.measurements.Measurements;
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
