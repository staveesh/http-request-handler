package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import com.taveeshsharma.requesthandler.utils.NoDuplicatesPriorityQueue;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("edf")
public class EDFCEAlgorithm implements SchedulingAlgorithm{

    private static final Logger logger = LoggerFactory.getLogger(EDFCEAlgorithm.class);

    /**
     * Performs scheduling in such a way that jobs with earliest deadlines get scheduled first.
     *
     * @param graph
     * @return
     */
    @Override
    public void preprocessJobs(ConflictGraph graph, List<String> devices) {
        logger.info("Preprocessing jobs using EDF-CE scheme");
        List<Job> jobs = graph.getJobs();
        jobs.sort((j1, j2) -> {
            if (j1.getNextReset().isBefore(j2.getNextReset()))
                return -1;
            else if (j1.getNextReset().equals(j2.getNextReset()))
                return 0;
            else
                return 1;
        });
    }

    @Override
    public Schedule  generateSchedule(List<Job> jobs,
                                      Map<Job, List<Job>> adjacencyMatrix, List<String> devices, Graph<String, DefaultEdge> netGraph){
        Map<Job, Assignment> jobAssignments = new HashMap<>();
        PriorityQueue<ZonedDateTime> schedulingPoints = new NoDuplicatesPriorityQueue<>((d1, d2) -> {
            if (d1.isBefore(d2))
                return -1;
            else if (d1.equals(d2))
                return 0;
            else
                return 1;
        });
        schedulingPoints.add(ZonedDateTime.now());
        List<Job> parallelJobs = new ArrayList<>();
        // Main logic
        while (!schedulingPoints.isEmpty()) {
            ZonedDateTime currentSchedulingPoint = schedulingPoints.poll();
            logger.info("Current scheduling point : " + currentSchedulingPoint.withZoneSameInstant(ZoneId.systemDefault()));
            // Reset parallel jobs list by removing the jobs that have finished execution
            parallelJobs.removeIf(job -> ApiUtils.addSeconds(jobAssignments.get(job).getDispatchTime(),
                    Constants.JOB_EXECUTION_TIMES.get(job.getType())).equals(currentSchedulingPoint));
            // Shuffle devices list at each scheduling point to ensure better job distribution
            Collections.shuffle(devices);
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
                        if(!hasConflicts && parallelJobs.size() < devices.size()){
                            DijkstraShortestPath sp = new DijkstraShortestPath(netGraph);
                            String src1 = jobAssignments.get(alreadyScheduledJob).getDeviceKey().toLowerCase();
                            String target1 = "t"+Integer.parseInt(alreadyScheduledJob.getParameters().getTarget().split("\\.")[3]);
                            List<DefaultEdge> path1 = sp.getPath(src1, target1).getEdgeList();
                            logger.info("Path1 : "+path1);
                            String src2 = devices.get(parallelJobs.size()).toLowerCase();
                            String target2 = "t"+Integer.parseInt(currentJob.getParameters().getTarget().split("\\.")[3]);
                            List<DefaultEdge> path2 = sp.getPath(src2, target2).getEdgeList();
                            logger.info("Path2 : "+path2);
                            for(DefaultEdge edge1 : path1){
                                for(DefaultEdge edge2: path2){
                                    if(edge1.equals(edge2)){
                                        hasConflicts = true;
                                        break;
                                    }
                                }
                                if(hasConflicts)
                                    break;
                            }
                        }
                    }
                    if(!hasConflicts && parallelJobs.size() < devices.size()) {
                        String deviceId = devices.get(parallelJobs.size());
                        logger.info(String.format("Scheduling Job ( key = %s, startTime = %s, endTime = %s) at %s on device %s",
                                currentJob.getKey(),
                                currentJob.getStartTime().withZoneSameInstant(ZoneId.systemDefault()),
                                currentJob.getEndTime().withZoneSameInstant(ZoneId.systemDefault()),
                                currentSchedulingPoint.withZoneSameInstant(ZoneId.systemDefault()),deviceId));
                        currentJob.setDispatchTime(currentSchedulingPoint);
                        jobAssignments.put(currentJob, new Assignment(currentSchedulingPoint,
                                deviceId));
                        schedulingPoints.add(ApiUtils.addSeconds(currentSchedulingPoint,
                                Constants.JOB_EXECUTION_TIMES.get(currentJob.getType())));
                        parallelJobs.add(currentJob);
                    }
                }
            }
        }
        Schedule schedule = new Schedule(ZonedDateTime.now(), jobAssignments);
        logger.info("Scheduling complete");
        printSchedule(jobAssignments);
        return schedule;
    }
}
