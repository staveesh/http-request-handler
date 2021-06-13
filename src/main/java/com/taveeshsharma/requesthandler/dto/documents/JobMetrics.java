package com.taveeshsharma.requesthandler.dto.documents;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Document("job_metrics")
@CompoundIndexes(value = { @CompoundIndex(name = "key_instance_no_idx", def = "{'instanceNumber':1, 'jobKey':1}", unique = true) })
public class JobMetrics {
    @Id
    private String id;
    private Integer instanceNumber;
    private String jobKey;
    private String nodeId;
    private Long executionTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime completionTime; // Time at which result is received from data collection node
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime addedToQueueAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime scheduleGeneratedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime dispatchTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Integer getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(Integer instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public ZonedDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(ZonedDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public ZonedDateTime getAddedToQueueAt() {
        return addedToQueueAt;
    }

    public void setAddedToQueueAt(ZonedDateTime addedToQueueAt) {
        this.addedToQueueAt = addedToQueueAt;
    }

    public ZonedDateTime getScheduleGeneratedAt() {
        return scheduleGeneratedAt;
    }

    public void setScheduleGeneratedAt(ZonedDateTime scheduleGeneratedAt) {
        this.scheduleGeneratedAt = scheduleGeneratedAt;
    }

    public ZonedDateTime getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(ZonedDateTime dispatchTime) {
        this.dispatchTime = dispatchTime;
    }
}
