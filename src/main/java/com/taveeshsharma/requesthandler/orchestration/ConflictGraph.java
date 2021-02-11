package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictGraph {
    private static final Logger logger = LoggerFactory.getLogger(ConflictGraph.class);

    private List<Job> jobs;
    Map<String, Map<String, Boolean>> conflictMatrix;

    public ConflictGraph(List<Job> jobs) {
        this.jobs = jobs;
        build();
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
            if(!newJob.equals(existingJob)) {
                // TODO: Need to advance the criteria for determining conflicts
                if (newJob.getParameters().getTarget().equalsIgnoreCase(existingJob
                        .getParameters().getTarget())) {
                    conflictMatrix.get(newJob.getKey()).put(existingJob.getKey(), true);
                } else {
                    conflictMatrix.get(newJob.getKey()).put(existingJob.getKey(), false);
                }
            }
        }
        logger.info(String.format("Added job with key %s to conflict graph", newJob.getKey()));
    }

    public void removeNode(Job toRemove){
        jobs.remove(toRemove);
        conflictMatrix.remove(toRemove.getKey());
        for(Job existingJob : jobs){
            conflictMatrix.get(existingJob.getKey()).remove(toRemove.getKey());
        }
        logger.info(String.format("Removed job with key %s from conflict graph", toRemove.getKey()));
    }

    public Map<String, Map<String, Boolean>> getConflictMatrix() {
        return conflictMatrix;
    }

    public void setConflictMatrix(Map<String, Map<String, Boolean>> conflictMatrix) {
        this.conflictMatrix = conflictMatrix;
    }

    private void build(){
        conflictMatrix = new HashMap<>();
        for(Job job : this.jobs) {
            addNode(job);
        }
    }
}
