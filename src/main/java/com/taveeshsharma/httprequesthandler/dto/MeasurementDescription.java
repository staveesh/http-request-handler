package com.taveeshsharma.httprequesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

public class MeasurementDescription {
    private String type;
    private String key;
    private String startTime;
    private String endTime;
    private Integer intervalSec;
    private Long count;
    private Long priority;
    private Parameters parameters;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public Integer getIntervalSec() {
        return intervalSec;
    }

    public void setIntervalSec(Integer intervalSec) {
        this.intervalSec = intervalSec;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "MeasurementDescription{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", intervalSec=" + intervalSec +
                ", count=" + count +
                ", priority=" + priority +
                ", parameters=" + parameters +
                '}';
    }
}
