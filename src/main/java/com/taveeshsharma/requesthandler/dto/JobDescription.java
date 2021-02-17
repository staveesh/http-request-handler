package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobDescription {

    private MeasurementDescription measurementDescription;
    private Integer nodeCount;
    private JobInterval jobInterval;

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

    @Override
    public String toString() {
        return "JobDescription{" +
                "measurementDescription=" + measurementDescription +
                ", nodeCount=" + nodeCount +
                ", jobInterval=" + jobInterval +
                '}';
    }
}
