package com.taveeshsharma.httprequesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "job_data")
public class ScheduleRequest {
    private String requestType;
    private JobDescription jobDescription;
    private String userId;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ScheduleRequest{" +
                "requestType='" + requestType + '\'' +
                ", jobDescription=" + jobDescription +
                ", userId='" + userId + '\'' +
                '}';
    }
}
