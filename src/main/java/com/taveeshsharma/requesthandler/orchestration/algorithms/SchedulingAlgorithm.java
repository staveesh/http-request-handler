package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public interface SchedulingAlgorithm {

    static final Logger logger = LoggerFactory.getLogger(SchedulingAlgorithm.class);

    public abstract void preprocessJobs(ConflictGraph graph, List<String> devices);

    public abstract Schedule generateSchedule(List<Job> jobs,
                                              Map<Job, List<Job>> adjacencyMatrix, List<String> devices, Graph<String, DefaultEdge> netGraph);

    public default void printSchedule(Map<Job, Assignment> schedule) {
        for (Map.Entry<Job, Assignment> jobAssignment : schedule.entrySet()) {
            logger.info(String.format("Job with key %s scheduled on device %s at time %s",
                    jobAssignment.getKey().getKey(),
                    jobAssignment.getValue().getDeviceKey(),
                    jobAssignment.getValue().getDispatchTime().withZoneSameInstant(ZoneId.systemDefault())));
        }
    }

}
