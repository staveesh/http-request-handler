package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;

import java.time.ZonedDateTime;
import java.util.Map;

public class Schedule {
    private ZonedDateTime generatedAt;
    private Map<Job, Assignment> jobAssignments;

    public Schedule(ZonedDateTime generatedAt, Map<Job, Assignment> jobAssignments) {
        this.generatedAt = generatedAt;
        this.jobAssignments = jobAssignments;
    }

    public ZonedDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(ZonedDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Map<Job, Assignment> getJobAssignments() {
        return jobAssignments;
    }

    public void setJobAssignments(Map<Job, Assignment> jobAssignments) {
        this.jobAssignments = jobAssignments;
    }
}
