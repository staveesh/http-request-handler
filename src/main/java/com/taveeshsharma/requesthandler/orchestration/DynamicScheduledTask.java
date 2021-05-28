package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;

import java.time.ZonedDateTime;

public class DynamicScheduledTask {
    private Job job;
    private String deviceId;
    private ZonedDateTime dispatchTime;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ZonedDateTime getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(ZonedDateTime dispatchTime) {
        this.dispatchTime = dispatchTime;
    }
}
