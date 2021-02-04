package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictGraph {
    private List<Job> jobs;
    Map<String, Map<String, Boolean>> conflictMatrix;

    public ConflictGraph(List<Job> jobs) {
        this.jobs = jobs;
        conflictMatrix = new HashMap<>();
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void addNode(Job newJob){
        conflictMatrix.put(newJob.getKey(), new HashMap<>());
        for(Job existingJob : jobs){
            if(newJob.conflictsWith(existingJob)){
                conflictMatrix.get(existingJob.getKey()).put(newJob.getKey(), true);
                conflictMatrix.get(newJob.getKey()).put(existingJob.getKey(), true);
            } else{
                conflictMatrix.get(existingJob.getKey()).put(newJob.getKey(), false);
                conflictMatrix.get(newJob.getKey()).put(existingJob.getKey(), false);
            }
        }
    }

    public Map<String, Map<String, Boolean>> getConflictMatrix() {
        return conflictMatrix;
    }

    public void setConflictMatrix(Map<String, Map<String, Boolean>> conflictMatrix) {
        this.conflictMatrix = conflictMatrix;
    }
}
