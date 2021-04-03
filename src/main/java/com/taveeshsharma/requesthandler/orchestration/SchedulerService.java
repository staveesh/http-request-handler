package com.taveeshsharma.requesthandler.orchestration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taveeshsharma.requesthandler.config.WebSocketConfig;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.dto.documents.JobMetrics;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.measurements.MobileDeviceMeasurement;
import com.taveeshsharma.requesthandler.orchestration.algorithms.SchedulingAlgorithm;
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
    @Qualifier("dosdAlgorithm")
    private SchedulingAlgorithm schedulingAlgorithm;

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final List<Job> activeJobs = new ArrayList<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void addMeasurement(Job job) {
        acquireWriteLock();
        if (job == null) return;
        activeJobs.add(job);
        JobMetrics metrics = new JobMetrics();
        int instanceNumber = job.getInstanceNumber().get();
        String key = job.getKey();
        metrics.setId(key+"-"+instanceNumber);
        metrics.setInstanceNumber(instanceNumber);
        metrics.setJobKey(key);
        metrics.setAddedToQueueAt(ZonedDateTime.now());
        dbManager.upsertJobMetrics(metrics);
        releaseWriteLock();
    }

    public void requestScheduling(ConflictGraph graph, List<String> devices) {
        acquireReadLock();
        if(devices == null){
            devices = new ArrayList<>(WebSocketConfig.connections.values());
        }
        if(graph == null){
            graph = new ConflictGraph(activeJobs);
            graph.buildDefault();
        }
        if (devices.size() > 0) {
            List<Job> processedJobs = schedulingAlgorithm.preprocessJobs(graph, devices);
            Schedule schedule = schedulingAlgorithm.generateSchedule(processedJobs,
                    graph.getAdjacencyMatrix(), devices);
            sendActiveJobs(schedule);
        } else {
            logger.error("Skipping scheduling as no devices have checked in recently");
        }
        releaseReadLock();
    }

    private void sendActiveJobs(Schedule jobSchedule) {
        Map<String, List<MeasurementDescription>> jobsToBeSent = new HashMap<>();
        if (jobSchedule.getJobAssignments().size() > 0) {
            ZonedDateTime currentTime = ZonedDateTime.now();
            for (Iterator<Map.Entry<Job, Assignment>> it = jobSchedule.getJobAssignments().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Job, Assignment> schedule = it.next();
                String deviceId = schedule.getValue().getDeviceKey();
                if(!jobsToBeSent.containsKey(deviceId))
                    jobsToBeSent.put(deviceId, new ArrayList<>());
                jobsToBeSent.get(deviceId).add(schedule.getKey().getMeasurementDescription());
                boolean dispatchTimeElapsed = currentTime.isAfter(schedule.getValue().getDispatchTime());
                boolean isJobNotRemovable = !schedule.getKey().isRemovable();
                boolean isJobNotResettable = !schedule.getKey().isResettable(currentTime);
                logger.info("Job key : "+schedule.getKey().getKey());
                logger.info("dispatchTimeElapsed = "+dispatchTimeElapsed+", isJobNotRemovable = "+isJobNotRemovable+
                " ,isJobNotResettable = "+isJobNotResettable);
                if (dispatchTimeElapsed && isJobNotRemovable && isJobNotResettable) {
                    Job job = schedule.getKey();
                    String jobKey = job.getKey();
                    int instanceNumber = job.getInstanceNumber().get();
                    JobMetrics metrics = dbManager.findMetricsById(jobKey+"-"+instanceNumber);
                    if(metrics == null){
                        metrics = new JobMetrics();
                        metrics.setId(jobKey+"-"+instanceNumber);
                        metrics.setInstanceNumber(instanceNumber);
                        metrics.setJobKey(jobKey);
                        metrics.setAddedToQueueAt(ZonedDateTime.now());
                    }
                    metrics.setScheduleGeneratedAt(jobSchedule.getGeneratedAt());
                    metrics.setExpectedDispatchTime(schedule.getValue().getDispatchTime());
                    metrics.setActualDispatchTime(ZonedDateTime.now());
                    dbManager.upsertJobMetrics(metrics);
                    it.remove();
                }
            }
        }
        for(Map.Entry<String, List<MeasurementDescription> > deviceJobs : jobsToBeSent.entrySet()){
            List<MeasurementDescription> jobs = deviceJobs.getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            try {
                messagingTemplate.convertAndSendToUser(
                        deviceJobs.getKey(),
                        "/queue/jobs",
                        objectMapper.writeValueAsString(jobs));
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
            if (currKey.equals(key) && jobDesc.getBoolean("success")) {
                job.addNodeCount();
                job.updateInstanceNumber();
                int instanceNumber = jobDesc.getJSONObject("parameters").getInt("instanceNumber");
                String nodeId = jobDesc.getJSONObject("properties").getString("deviceId");
                JobMetrics metrics = dbManager.findMetricsById(key+"-"+instanceNumber);
                if(metrics == null)
                    continue;
                metrics.setCompletionTime(completionTime);
                metrics.setNodeId(nodeId);
                metrics.setExecutionTime(jobDesc.getLong("executionTime"));
                logger.info("Job with key : " + currKey + "has been incremented by one");
                if (job.nodesReached()) logger.info("\nJobs with Key " + key + " has Reached its Req Node count\n");
                dbManager.upsertJobMetrics(metrics);
                dbManager.upsertJob(job);
            }
        }
        releaseReadLock();
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
}
