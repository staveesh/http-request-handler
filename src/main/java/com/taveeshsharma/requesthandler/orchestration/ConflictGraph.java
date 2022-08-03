package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConflictGraph {
    private static final Logger logger = LoggerFactory.getLogger(ConflictGraph.class);

    private List<Job> jobs;
    Map<Job, List<Job>> adjacencyMatrix;

    public ConflictGraph(List<Job> jobs) {
        this.jobs = jobs;
        adjacencyMatrix = new HashMap<>();
    }

    public void addEdge(Job j1, Job j2) {
        if (!adjacencyMatrix.containsKey(j1)) {
            adjacencyMatrix.put(j1, new ArrayList<>());
        }
        adjacencyMatrix.get(j1).add(j2);

        if (!adjacencyMatrix.containsKey(j2)) {
            adjacencyMatrix.put(j2, new ArrayList<>());
        }
        adjacencyMatrix.get(j2).add(j1);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void addNode(Job newJob) {
        adjacencyMatrix.put(newJob, new ArrayList<>());
        for (Job existingJob : jobs) {
            if (!newJob.equals(existingJob)) {
                if (newJob.getParameters().getTarget().equalsIgnoreCase(existingJob
                        .getParameters().getTarget())) {
                    adjacencyMatrix.get(newJob).add(existingJob);
                    adjacencyMatrix.get(existingJob).add(newJob);
                }
            }
        }
        logger.info(String.format("Added job with key %s to conflict graph", newJob.getKey()));
    }

    public Map<Job, List<Job>> getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public void setAdjacencyMatrix(Map<Job, List<Job>> adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public void buildDefault() {

        for (Job job : jobs) adjacencyMatrix.put(job, new ArrayList<>());

        for (int idx1 = 0; idx1 < jobs.size() - 1; idx1++) {
            Job j1 = jobs.get(idx1);
            for (int idx2 = idx1 + 1; idx2 < jobs.size(); idx2++) {
                Job j2 = jobs.get(idx2);
                if (j1.getParameters().getTarget().equals(j2.getParameters().getTarget())) {
                    adjacencyMatrix.get(j1).add(j2);
                    adjacencyMatrix.get(j2).add(j1);
                }
            }
        }
    }
}
