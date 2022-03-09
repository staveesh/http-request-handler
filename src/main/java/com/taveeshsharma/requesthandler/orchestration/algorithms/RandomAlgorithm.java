package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Qualifier("random")
public class RandomAlgorithm implements SchedulingAlgorithm{
    @Override
    public void preprocessJobs(ConflictGraph graph, List<String> devices) {
        logger.info("Preprocessing jobs using Random scheme");
    }

    @Override
    public Schedule generateSchedule(List<Job> jobs, Map<Job, List<Job>> adjacencyMatrix, List<String> devices,
                                     Graph<String, DefaultEdge> netGraph) {
        Map<Job, Assignment> jobAssignments = new HashMap<>();
        for(Job job : jobs){
            // Assign jobs to a random device
            job.setDispatchTime(job.getStartTime());
            jobAssignments.put(job, new Assignment(job.getStartTime(), devices.get(new Random().nextInt(devices.size()))));
        }
        Schedule schedule = new Schedule(ZonedDateTime.now(), jobAssignments);
        logger.info("Scheduling complete");
        printSchedule(jobAssignments);
        return schedule;
    }
}
