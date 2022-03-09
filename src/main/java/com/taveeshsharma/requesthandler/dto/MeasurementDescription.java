package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class MeasurementDescription {

    private String type;
    private String key;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime endTime;
    private Integer intervalSec;
    private Long count;
    private Long priority;
    private Parameters parameters;
    private Integer instanceNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime addedToQueueAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime dispatchTime;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("startTime")
    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("endTime")
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("intervalSec")
    public Integer getIntervalSec() {
        return intervalSec;
    }

    public void setIntervalSec(Integer intervalSec) {
        this.intervalSec = intervalSec;
    }

    @JsonProperty("count")
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @JsonProperty("priority")
    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    @JsonProperty("parameters")
    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @JsonProperty("instanceNumber")
    public Integer getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(Integer instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    @JsonProperty("addedToQueueAt")
    public ZonedDateTime getAddedToQueueAt() {
        return addedToQueueAt;
    }

    public void setAddedToQueueAt(ZonedDateTime addedToQueueAt) {
        this.addedToQueueAt = addedToQueueAt;
    }

    @JsonProperty("dispatchTime")
    public ZonedDateTime getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(ZonedDateTime dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    @Override
    public String toString() {
        return "MeasurementDescription{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", intervalSec=" + intervalSec +
                ", count=" + count +
                ", priority=" + priority +
                ", parameters=" + parameters +
                ", instanceNumber=" + instanceNumber +
                ", addedToQueueAt=" + addedToQueueAt +
                ", dispatchTime=" + dispatchTime +
                '}';
    }
}
