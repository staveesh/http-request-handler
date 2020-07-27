package com.taveeshsharma.httprequesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobDescription {
    private MeasurementDescription measurementDescription;
    private Integer nodeCount;
    private Integer jobInterval;

    public MeasurementDescription getMeasurementDescription() {
        return measurementDescription;
    }

    public void setMeasurementDescription(MeasurementDescription measurementDescription) {
        this.measurementDescription = measurementDescription;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }

    public Integer getJobInterval() {
        return jobInterval;
    }

    public void setJobInterval(Integer jobInterval) {
        this.jobInterval = jobInterval;
    }

    @Override
    public String toString() {
        return "JobDescription{" +
                "measurementDescription=" + measurementDescription +
                ", nodeCount=" + nodeCount +
                ", jobInterval=" + jobInterval +
                '}';
    }
}
