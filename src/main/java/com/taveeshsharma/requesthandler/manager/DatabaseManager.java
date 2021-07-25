package com.taveeshsharma.requesthandler.manager;

import com.taveeshsharma.requesthandler.dto.documents.*;
import com.taveeshsharma.requesthandler.measurements.AccessPointMeasurement;
import com.taveeshsharma.requesthandler.measurements.Measurements;
import com.taveeshsharma.requesthandler.measurements.MobileDeviceMeasurement;
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

    public List<Job> getCurrentlyActiveJobs(Date currentTime);

    public void upsertJob(Job job);

    public void writeAccessPointInfo(JSONObject accessPointMeasurement);

    public List<MobileDeviceMeasurement> getAvailableDevices();

    public void writeMobileDeviceInfo(JSONObject mobileDevice);

    public List<AccessPointMeasurement> getAllAccessPoints(String deviceId);

    public JobMetrics findMetricsById(String id);

    public void upsertJobMetrics(JobMetrics metrics);

    public String findLastSummaryCheckinTime(String deviceId);

    public Cipher findBestCipherForLevel(String level);

    public Filter findBestFilter(String networkType, String level);
}
