package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobDescription {

    private MeasurementDescription measurementDescription;
    private Integer nodeCount;
    private JobInterval jobInterval;
    private String deviceId;

    @JsonProperty("measurementDescription")
    public MeasurementDescription getMeasurementDescription() {
        return measurementDescription;
    }

    public void setMeasurementDescription(MeasurementDescription measurementDescription) {
        this.measurementDescription = measurementDescription;
    }
    @JsonProperty("nodeCount")
    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }
    @JsonProperty("jobInterval")
    public JobInterval getJobInterval() {
        return jobInterval;
    }

    public void setJobInterval(JobInterval jobInterval) {
        this.jobInterval = jobInterval;
    }

    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
