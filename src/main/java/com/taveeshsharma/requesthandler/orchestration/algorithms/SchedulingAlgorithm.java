package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SchedulingAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingAlgorithm.class);

    public abstract List<Job> preprocessJobs(ConflictGraph graph, List<String> devices);

    public abstract Schedule generateSchedule(List<Job> jobs,
                                              Map<Job, List<Job>> adjacencyMatrix, List<String> devices);

    public ZonedDateTime getFirstSchedulingPoint(List<Job> jobs) {
        ZonedDateTime result = jobs.get(0).getStartTime();
        for (Job job : jobs) {
            if (job.getStartTime().isBefore(result))
                result = job.getStartTime();
        }
        return result;
    }

    void printSchedule(Map<Job, Assignment> schedule) {
        for (Map.Entry<Job, Assignment> jobAssignment : schedule.entrySet()) {
            logger.info(String.format("Job with key %s scheduled on device %s at time %s",
                    jobAssignment.getKey().getKey(),
                    jobAssignment.getValue().getDeviceKey(),
                    jobAssignment.getValue().getDispatchTime().withZoneSameInstant(ZoneId.systemDefault())));
        }
    }

}
