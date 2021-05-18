package com.taveeshsharma.requesthandler.orchestration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taveeshsharma.requesthandler.config.WebSocketConfig;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.dto.documents.JobMetrics;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.algorithms.SchedulingAlgorithm;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.json.JSONArray;
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
    @Qualifier("edfCeAlgorithm")
    private SchedulingAlgorithm schedulingAlgorithm;

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

    public void requestScheduling(ConflictGraph graph, List<String> devices) {
        acquireReadLock();
        if (devices == null) {
            devices = new ArrayList<>(WebSocketConfig.connections.values());
        }
        if (devices.size() == 0) {
            logger.error("Skipping scheduling as no devices have checked in recently");
            releaseReadLock();
            return;
        }
        if (graph == null) {
            ZonedDateTime currentTime = ZonedDateTime.now();
            // Schedule only those jobs for which start time has passed and they have not been scheduled before
            List<Job> jobsToSchedule = activeJobs
                    .stream().filter(job ->
                            currentTime.isAfter(job.getStartTime()) && job.getDispatchTime() == null
                    )
                    .collect(Collectors.toList());
            if (jobsToSchedule.size() == 0) {
                logger.error("Skipping scheduling as no new jobs start after present time");
                releaseReadLock();
                return;
            }
            graph = new ConflictGraph(jobsToSchedule);
            graph.buildDefault();
        }
        schedulingAlgorithm.preprocessJobs(graph, devices);
        Schedule newSchedule = schedulingAlgorithm.generateSchedule(graph.getJobs(),
                graph.getAdjacencyMatrix(), devices);
        sendActiveJobs(newSchedule);
        releaseReadLock();
    }

    private void sendActiveJobs(Schedule theSchedule) {
        Map<String, List<MeasurementDescription>> jobsToBeSent = new HashMap<>();
        if (theSchedule.getJobAssignments().size() > 0) {
            ZonedDateTime currentTime = ZonedDateTime.now();
            for (Iterator<Map.Entry<Job, Assignment>> it = theSchedule.getJobAssignments().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Job, Assignment> schedule = it.next();
                String deviceId = schedule.getValue().getDeviceKey();
                boolean dispatchTimeElapsed = currentTime.isAfter(schedule.getValue().getDispatchTime());
                boolean isJobNotRemovable = !schedule.getKey().isRemovable();
                boolean isJobNotResettable = !schedule.getKey().isResettable(currentTime);
                String instanceKey = schedule.getKey().getKey() + "-" + schedule.getKey().getInstanceNumber().get();
                boolean instanceNotDispatchedBefore = !jobInstanceTracker.contains(instanceKey);
                logger.info("instanceKey = " + instanceKey +
                        ", instanceNotDispatchedBefore = " + instanceNotDispatchedBefore +
                        ", dispatchTimeElapsed = " + dispatchTimeElapsed +
                        ", isJobNotRemovable = " + isJobNotRemovable +
                        " ,isJobNotResettable = " + isJobNotResettable);
                if (instanceNotDispatchedBefore && dispatchTimeElapsed && isJobNotRemovable && isJobNotResettable) {
                    Job job = schedule.getKey();
                    String jobKey = job.getKey();
                    if (!jobsToBeSent.containsKey(deviceId))
                        jobsToBeSent.put(deviceId, new ArrayList<>());
                    jobsToBeSent.get(deviceId).add(job.getMeasurementDescription());
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
        for (Map.Entry<String, List<MeasurementDescription>> deviceJobs : jobsToBeSent.entrySet()) {
            List<MeasurementDescription> jobs = deviceJobs.getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            try {
                JSONArray jobsArray = new JSONArray(objectMapper.writeValueAsString(jobs));
                for (int i = 0; i < jobsArray.length(); i++) {
                    JSONObject jobObject = jobsArray.getJSONObject(i);
                    JSONObject parameters = new JSONObject();
                    switch (jobObject.getString("type")) {
                        case Constants.PING_TYPE:
                        case Constants.HTTP_TYPE:
                        case Constants.TRACERT_TYPE:
                            parameters.put("target", jobs.get(i).getParameters().getTarget());
                            break;
                        case Constants.DNS_TYPE:
                            parameters.put("target", jobs.get(i).getParameters().getTarget());
                            parameters.put("server", "null");
                            break;
                        case Constants.TCP_TYPE:
                            parameters.put("target", "custom");
                            parameters.put("dir_up", jobs.get(i).getParameters().getDirUp() ? "true" : "false");
                            break;
                    }
                    jobObject.put("parameters", parameters);
                }
                messagingTemplate.convertAndSendToUser(
                        deviceJobs.getKey(),
                        "/queue/jobs",
                        jobsArray.toString());
            } catch (JsonProcessingException e) {
                logger.error("Error converting jobs to valid JSON");
            }
        }
        logger.info("Active Jobs Sent To Phones");
    }

    public void recordSuccessfulJob(JSONObject jobDesc) {
        acquireReadLock();
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
        releaseReadLock();
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
