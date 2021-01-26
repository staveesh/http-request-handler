package com.taveeshsharma.requesthandler.manager;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import com.taveeshsharma.requesthandler.dto.documents.ScheduleRequest;
import com.taveeshsharma.requesthandler.measurements.Measurements;
import com.taveeshsharma.requesthandler.measurements.PcapMeasurements;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface DatabaseManager {
    public void insertScheduledJob(ScheduleRequest request);

    public List<? extends Measurements> getMeasurement(String id, String type);

    public List<ScheduleRequest> getScheduledJobs(String type);

    public void writeValues(JSONObject jsonObject);

    public void writePersonalData(PersonalData data);

    public void writePcapData(List<PcapMeasurements> pcapData);

    public List<PersonalData> readPersonalData(String email);

    public List<Job> getCurrentlyActiveJobs(Date currentTime);

    public void upsertJob(Job job);
}
