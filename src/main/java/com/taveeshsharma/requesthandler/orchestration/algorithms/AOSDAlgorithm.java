package com.taveeshsharma.requesthandler.orchestration.algorithms;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.network.NetworkNode;
import com.taveeshsharma.requesthandler.orchestration.Assignment;
import com.taveeshsharma.requesthandler.orchestration.ColorAssignment;
import com.taveeshsharma.requesthandler.orchestration.ConflictGraph;
import com.taveeshsharma.requesthandler.orchestration.Schedule;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import com.taveeshsharma.requesthandler.utils.NoDuplicatesPriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("aosdAlgorithm")
public class AOSDAlgorithm extends SchedulingAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(AOSDAlgorithm.class);

    @Autowired
    private List<NetworkNode> networkCosts;

    @Override
    public void preprocessJobs(ConflictGraph graph, List<String> devices) {
        Map<Job, List<Job>> adjacencyMatrix = graph.getAdjacencyMatrix();
        List<Job> jobs = graph.getJobs();
        jobs.sort(Comparator.comparingLong(j -> Constants.JOB_EXECUTION_TIMES.get(j.getType()) +
                adjacencyMatrix.get(j).size()));
    }

    private List<ColorAssignment> mergeColorRanges(List<ColorAssignment> rangesToMerge) {
        logger.info("Ranges to merge : " + rangesToMerge);
        if (rangesToMerge.size() == 0)
            return rangesToMerge;
        rangesToMerge.sort(Comparator.comparing(ColorAssignment::getStart));
        int index = 0;
        for (int i = 1; i < rangesToMerge.size(); i++) {
            if (rangesToMerge.get(index).getEnd() >= rangesToMerge.get(i).getStart()) {
                ColorAssignment merged = new ColorAssignment(
                        Math.min(rangesToMerge.get(index).getStart(), rangesToMerge.get(i).getStart()),
                        Math.max(rangesToMerge.get(index).getEnd(), rangesToMerge.get(i).getEnd())
                );
                rangesToMerge.set(index, merged);
            } else {
                index++;
                rangesToMerge.set(index, rangesToMerge.get(i));
            }
        }
        return rangesToMerge.subList(0, index + 1);
    }

    private void removeColorRanges(List<Integer> availableColors, List<ColorAssignment> rangesToRemove) {
        rangesToRemove = mergeColorRanges(rangesToRemove);
        for (ColorAssignment range : rangesToRemove) {
            availableColors.removeIf(color -> (color >= range.getStart() && color <= range.getEnd()));
        }
    }

    public int findFirstConsecutiveSequence(List<Integer> availableColors, int reqCount) {
        int index = 1;
        int currCount = 1;
        int prev = availableColors.get(0);
        while (index < availableColors.size()) {
            int current = availableColors.get(index);
            if (currCount == reqCount)
                break;
            if (current == prev + 1) {
                currCount++;
            } else {
                currCount = 1;
            }
            prev = current;
            index++;
        }
        return index - reqCount;
    }

    @Override
    public Schedule generateSchedule(List<Job> jobs,
                                     Map<Job, List<Job>> adjacencyMatrix, List<String> devices) {
        // Remove devices that have disconnected
        List<NetworkNode> mNodes = new ArrayList<>(networkCosts);
        if(mNodes.size() > devices.size())
            mNodes.removeIf(node -> Integer.parseInt(node.getLabel().substring(1)) > devices.size());
        Map<Job, Assignment> jobAssignments = new HashMap<>();
        Map<Job, ColorAssignment> colorLookup = new HashMap<>();
        PriorityQueue<ZonedDateTime> schedulingPoints = new NoDuplicatesPriorityQueue<>((d1, d2) -> {
            if (d1.isBefore(d2))
                return -1;
            else if (d1.equals(d2))
                return 0;
            else
                return 1;
        });
        ZonedDateTime firstSchedulingPoint = ZonedDateTime.now();
        schedulingPoints.add(firstSchedulingPoint);
        // Maximum number of colors to be used is same as sum of execution time units
        int colors = 0;
        for (Job job : jobs) {
            colors += Constants.JOB_EXECUTION_TIMES.get(job.getType());
        }
        List<Integer> allColors = new ArrayList<>();
        for (int i = 1; i <= colors; i++) {
            allColors.add(i);
        }
        List<Job> parallelJobs = new ArrayList<>();
        int slotStart = 1;
        while (!schedulingPoints.isEmpty()) {
            ZonedDateTime currentSchedulingPoint = schedulingPoints.poll();
            slotStart = 1 + (int) ChronoUnit.SECONDS.between(firstSchedulingPoint, currentSchedulingPoint);
            logger.info("Current scheduling point : " + currentSchedulingPoint.withZoneSameInstant(ZoneId.systemDefault()));
            Set<Integer> devicesToExclude = new HashSet<>();
            ListIterator<Job> iter = parallelJobs.listIterator();
            int remIdx = 0;
            while(iter.hasNext()){
                Job job = iter.next();
                if(ApiUtils.addSeconds(jobAssignments.get(job).getDispatchTime(),
                        Constants.JOB_EXECUTION_TIMES.get(job.getType())).equals(currentSchedulingPoint))
                    iter.remove();
                else
                    devicesToExclude.add(remIdx);
                remIdx++;
            }
            for (Job currentJob : jobs) {
                if (!jobAssignments.containsKey(currentJob)) {
                    logger.info("Current Job : " + currentJob.getKey());
                    logger.info("Parallel Jobs : "+parallelJobs.stream().map(Job::getKey).collect(Collectors.toList()));
                    List<Integer> availableColors = new ArrayList<>(allColors);
                    // Remove colors that have already been assigned
                    List<ColorAssignment> rangesToRemove = new ArrayList<>();
                    for (Job conflictingJob : adjacencyMatrix.get(currentJob)) {
                        if (colorLookup.containsKey(conflictingJob)) {
                            rangesToRemove.add(colorLookup.get(conflictingJob));
                        }
                    }
                    removeColorRanges(availableColors, rangesToRemove);
                    int numberOfSlots = Constants.JOB_EXECUTION_TIMES.get(currentJob.getType()).intValue();
                    int startIndex = findFirstConsecutiveSequence(availableColors, numberOfSlots);
                    if(startIndex + numberOfSlots <= availableColors.size()) {
                        int start = availableColors.get(startIndex);
                        int end = availableColors.get(startIndex + numberOfSlots - 1);
                        if (start == slotStart && parallelJobs.size() < devices.size()) {
                            ColorAssignment assignedRange = new ColorAssignment(start, end);
                            colorLookup.put(currentJob, assignedRange);
                            schedulingPoints.add(currentSchedulingPoint.plusSeconds(numberOfSlots));
                            double prob = Math.random();
                            int idx;
                            for(idx = parallelJobs.size(); idx < mNodes.size(); idx++){
                                if (mNodes.get(idx).getProbAssignment() >= prob && !devicesToExclude.contains(idx))
                                    break;
                            }
                            String deviceNumber = mNodes.get(parallelJobs.size()).getLabel();
                            if(idx < mNodes.size())
                                deviceNumber = mNodes.get(idx).getLabel();
                            String deviceId = devices.get(Integer.parseInt(deviceNumber.substring(1))-1);
                            jobAssignments.put(currentJob, new Assignment(currentSchedulingPoint, deviceId));
                            currentJob.setDispatchTime(currentSchedulingPoint);
                            parallelJobs.add(currentJob);
                            schedulingPoints.add(ApiUtils.addSeconds(currentSchedulingPoint, numberOfSlots));
                        }
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
