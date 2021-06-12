package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.config.WebSocketConfig;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.dto.documents.JobMetrics;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.algorithms.SchedulingAlgorithm;
import com.taveeshsharma.requesthandler.utils.NoDuplicatesPriorityQueue;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    @Qualifier("roundRobinAlgorithm")
    private SchedulingAlgorithm schedulingAlgorithm;

    @Autowired
    private JobDispatcher dispatcher;

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final List<Job> activeJobs = new ArrayList<>();
    private final Set<String> jobInstanceTracker = new HashSet<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void addMeasurement(Job job) {
        acquireWriteLock();
        if (job == null) return;
        activeJobs.add(job);
        insertNewJobMetrics(job);
        releaseWriteLock();
    }

    private void insertNewJobMetrics(Job job) {
        JobMetrics metrics = new JobMetrics();
        int instanceNumber = job.getInstanceNumber().get();
        String key = job.getKey();
        metrics.setId(key + "-" + instanceNumber);
        metrics.setInstanceNumber(instanceNumber);
        metrics.setJobKey(key);
        metrics.setAddedToQueueAt(ZonedDateTime.now());
        dbManager.upsertJobMetrics(metrics);
    }

    public Schedule requestScheduling(ConflictGraph graph, List<String> devices) {
        acquireReadLock();
        if (devices == null) {
            devices = new ArrayList<>(WebSocketConfig.connections.values());
        }
        if (devices.size() == 0) {
            logger.error("Skipping scheduling as no devices have checked in recently");
            releaseReadLock();
            return null;
        }
        if (graph == null) {
            ZonedDateTime currentTime = ZonedDateTime.now();
            List<Job> jobsToSchedule = activeJobs
                    .stream().filter(job -> {
                        boolean startsNow = currentTime.isAfter(job.getStartTime());
                        String instanceKey = job.getKey() + "-" + job.getInstanceNumber().get();
                        boolean instanceNotDispatchedBefore = !jobInstanceTracker.contains(instanceKey);
                        return startsNow && instanceNotDispatchedBefore;
                    })
                    .collect(Collectors.toList());
            if (jobsToSchedule.size() == 0) {
                logger.error("Skipping scheduling as no new jobs start after present time");
                releaseReadLock();
                return null;
            }
            graph = new ConflictGraph(jobsToSchedule);
            graph.buildDefault();
        }
        schedulingAlgorithm.preprocessJobs(graph, devices);
        Schedule newSchedule = schedulingAlgorithm.generateSchedule(graph.getJobs(),
                graph.getAdjacencyMatrix(), devices);
        releaseReadLock();
        return newSchedule;
    }

    public void sendActiveJobs(Schedule theSchedule) {
        if (theSchedule != null && theSchedule.getJobAssignments().size() > 0) {
            ZonedDateTime currentTime = ZonedDateTime.now();
            for (Iterator<Map.Entry<Job, Assignment>> it = theSchedule.getJobAssignments().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Job, Assignment> schedule = it.next();
                boolean isJobNotRemovable = !schedule.getKey().isRemovable();
                boolean isJobNotResettable = !schedule.getKey().isResettable(currentTime);
                String instanceKey = schedule.getKey().getKey() + "-" + schedule.getKey().getInstanceNumber().get();
                boolean instanceNotDispatchedBefore = !jobInstanceTracker.contains(instanceKey);
                logger.info("instanceKey = " + instanceKey +
                        ", instanceNotDispatchedBefore = " + instanceNotDispatchedBefore +
                        ", isJobNotRemovable = " + isJobNotRemovable +
                        " ,isJobNotResettable = " + isJobNotResettable);
                if (instanceNotDispatchedBefore && isJobNotRemovable && isJobNotResettable) {
                    Job job = schedule.getKey();
                    DispatchTask task = new DispatchTask();
                    task.setJob(job);
                    task.setDispatchTime(schedule.getValue().getDispatchTime());
                    task.setDeviceId(schedule.getValue().getDeviceKey());
                    dispatcher.addNewTask(task);
                    String jobKey = job.getKey();
                    int instanceNumber = job.getInstanceNumber().get();
                    JobMetrics metrics = dbManager.findMetricsById(jobKey + "-" + instanceNumber);
                    metrics.setScheduleGeneratedAt(theSchedule.getGeneratedAt());
                    metrics.setExpectedDispatchTime(schedule.getValue().getDispatchTime());
                    metrics.setActualDispatchTime(ZonedDateTime.now());
                    dbManager.upsertJobMetrics(metrics);
                    jobInstanceTracker.add(instanceKey);
                    it.remove();
                }
            }
        }
    }

    public void recordSuccessfulJob(JSONObject jobDesc) {
        acquireWriteLock();
        ZonedDateTime completionTime = ZonedDateTime.now();
        //assuming the JsonObj has key field mapping which measurement failed
        String key = jobDesc.getString("taskKey");
        for (Job job : activeJobs) {
            String currKey = job.getKey();
            if (currKey.equals(key)) {
                int instanceNumber = jobDesc.getJSONObject("parameters").getInt("instanceNumber");
                String nodeId = jobDesc.getJSONObject("properties").getString("deviceId");
                JobMetrics metrics = dbManager.findMetricsById(key + "-" + instanceNumber);
                metrics.setCompletionTime(completionTime);
                metrics.setNodeId(nodeId);
                metrics.setExecutionTime(jobDesc.getLong("executionTime"));
                dbManager.upsertJob(job);
                dbManager.upsertJobMetrics(metrics);
            }
        }
        releaseWriteLock();
        if (jobDesc.getBoolean("success"))
            dbManager.writeValues(jobDesc);
    }

    public void acquireReadLock() {
        readWriteLock.readLock().lock();
    }

    public void releaseReadLock() {
        readWriteLock.readLock().unlock();
    }

    public void acquireWriteLock() {
        readWriteLock.writeLock().lock();
    }

    public void releaseWriteLock() {
        readWriteLock.writeLock().unlock();
    }

    public List<Job> getJobs() {
        return activeJobs;
    }

    public Set<String> getJobInstanceTracker() {
        return jobInstanceTracker;
    }
}
