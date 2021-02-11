package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SchedulingAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingAlgorithm.class);

    public abstract List<Job> preprocessJobs(ConflictGraph graph, List<String> devices);

    public Map<Job, Assignment> generateSchedule(List<Job> jobs, Map<String,
            Map<String, Boolean>> conflictMatrix, List<String> devices){
        Map<Job, Assignment> schedule = new HashMap<>();
        Queue<Date> schedulingPoints = new PriorityQueue<>((d1, d2) -> {
            if (d1.before(d2))
                return -1;
            else if (d1.equals(d2))
                return 0;
            else
                return 1;
        });
        Date currentTime = new Date();
        schedulingPoints.add(currentTime);
        List<Job> parallelJobs = new ArrayList<>();
        // Main logic
        while (!schedulingPoints.isEmpty()) {
            Date currentSchedulingPoint = schedulingPoints.poll();
            logger.info("Current scheduling point : " + currentSchedulingPoint);
            // Reset parallel jobs list by removing the jobs that have finished execution
            parallelJobs.removeIf(job -> ApiUtils.addMinutes(schedule.get(job).getDispatchTime(),
                    Constants.JOB_EXECUTION_TIMES.get(job.getType())).equals(currentSchedulingPoint));
            for (Job currentJob : jobs) {
                logger.info("Current job : "+currentJob.getKey());
                logger.info("Parallel Jobs : "+parallelJobs.stream().map(Job::getKey).collect(Collectors.toList()));
                if (!schedule.containsKey(currentJob)) {
                    boolean hasConflicts = false;
                    for (Job alreadyScheduledJob : parallelJobs) {
                        // If current job doesn't conflict with already running parallel job, schedule it
                        if (conflictMatrix.get(alreadyScheduledJob.getKey()).get(currentJob.getKey())) {
                            hasConflicts = true;
                        }
                    }
                    if(!hasConflicts && parallelJobs.size() < devices.size()) {
                        parallelJobs.add(currentJob);
                        String deviceId = devices.get(parallelJobs.size()-1);
                        logger.info(String.format("Scheduling Job ( key = %s, startTime = %s, endTime = %s) at %s on device %s",
                                currentJob.getKey(), currentJob.getStartTime(), currentJob.getEndTime(), currentSchedulingPoint,deviceId));
                        schedule.put(currentJob, new Assignment(currentSchedulingPoint,
                                deviceId));
                        schedulingPoints.add(ApiUtils.addMinutes(currentSchedulingPoint,
                                Constants.JOB_EXECUTION_TIMES.get(currentJob.getType())));
                    }
                }
            }
        }
        logger.info("Scheduling complete");
        printSchedule(schedule);
        return schedule;
    }

    private void printSchedule(Map<Job, Assignment> schedule){
        for(Map.Entry<Job, Assignment> jobAssignment : schedule.entrySet()){
            logger.info(String.format("Job with key %s scheduled on device %s at time %s",
                    jobAssignment.getKey().getKey(),
                    jobAssignment.getValue().getDeviceKey(),
                    jobAssignment.getValue().getDispatchTime()));
        }
    }

}
