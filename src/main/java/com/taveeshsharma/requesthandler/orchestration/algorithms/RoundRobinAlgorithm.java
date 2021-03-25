package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import com.taveeshsharma.requesthandler.utils.NoDuplicatesPriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("roundRobinAlgorithm")
public class RoundRobinAlgorithm extends SchedulingAlgorithm {
    private static final Logger logger = LoggerFactory.getLogger(RoundRobinAlgorithm.class);

    /**
     * Performs scheduling in such a way that jobs that arrive first get scheduled first.
     *
     * @param graph
     * @return
     */
    @Override
    public List<Job> preprocessJobs(ConflictGraph graph, List<String> devices) {
        logger.info("Preprocessing jobs using Round Robin scheme");
        List<Job> jobs = graph.getJobs();
        jobs.removeIf(Job::isRemovable);
        jobs.sort((j1, j2) -> {
            if (j1.getStartTime().isBefore(j2.getStartTime()))
                return -1;
            else if (j1.getStartTime().equals(j2.getStartTime()))
                return 0;
            else
                return 1;
        });
        return jobs;
    }

    @Override
    public Schedule  generateSchedule(List<Job> jobs,
                                                 Map<Job, List<Job>> adjacencyMatrix, List<String> devices){
        Map<Job, Assignment> jobAssignments = new HashMap<>();
        PriorityQueue<ZonedDateTime> schedulingPoints = new NoDuplicatesPriorityQueue<>((d1, d2) -> {
            if (d1.isBefore(d2))
                return -1;
            else if (d1.equals(d2))
                return 0;
            else
                return 1;
        });
        schedulingPoints.add(getFirstSchedulingPoint(jobs));
        List<Job> parallelJobs = new ArrayList<>();
        // Main logic
        while (!schedulingPoints.isEmpty()) {
            ZonedDateTime currentSchedulingPoint = schedulingPoints.poll();
            logger.info("Current scheduling point : " + currentSchedulingPoint.withZoneSameInstant(ZoneId.systemDefault()));
            // Reset parallel jobs list by removing the jobs that have finished execution
            parallelJobs.removeIf(job -> ApiUtils.addMinutes(jobAssignments.get(job).getDispatchTime(),
                    Constants.JOB_EXECUTION_TIMES.get(job.getType())).equals(currentSchedulingPoint));
            for (Job currentJob : jobs) {
                logger.info("Current job : "+currentJob.getKey());
                logger.info("Parallel Jobs : "+parallelJobs.stream().map(Job::getKey).collect(Collectors.toList()));
                if (!jobAssignments.containsKey(currentJob)) {
                    boolean hasConflicts = false;
                    for (Job alreadyScheduledJob : parallelJobs) {
                        // If current job doesn't conflict with already running parallel job, schedule it
                        if (adjacencyMatrix.get(alreadyScheduledJob).contains(currentJob)) {
                            hasConflicts = true;
                        }
                    }
                    if(!hasConflicts && parallelJobs.size() < devices.size()) {
                        String deviceId = devices.get(parallelJobs.size());
                        logger.info(String.format("Scheduling Job ( key = %s, startTime = %s, endTime = %s) at %s on device %s",
                                currentJob.getKey(),
                                currentJob.getStartTime().withZoneSameInstant(ZoneId.systemDefault()),
                                currentJob.getEndTime().withZoneSameInstant(ZoneId.systemDefault()),
                                currentSchedulingPoint.withZoneSameInstant(ZoneId.systemDefault()),deviceId));
                        jobAssignments.put(currentJob, new Assignment(currentSchedulingPoint,
                                deviceId));
                        schedulingPoints.add(ApiUtils.addMinutes(currentSchedulingPoint,
                                Constants.JOB_EXECUTION_TIMES.get(currentJob.getType())));
                        parallelJobs.add(currentJob);
                    }
                }
            }
        }
        Schedule schedule = new Schedule(ZonedDateTime.now(), jobAssignments);
        logger.info("Scheduling complete");
        super.printSchedule(jobAssignments);
        return schedule;
    }

}