package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.measurements.MobileDeviceMeasurement;
import com.taveeshsharma.requesthandler.orchestration.algorithms.SchedulingAlgorithm;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private SchedulingAlgorithm roundRobinAlgorithm;

    @Autowired
    private DatabaseManager dbManager;

    private final List<Job> activeJobs = new ArrayList<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Map<Job, Assignment> jobSchedule = new HashMap<>();

    public void addMeasurement(Job job) {
        acquireWriteLock();
        if (job == null) return;
        activeJobs.add(job);
        requestScheduling();
        releaseWriteLock();
    }

    public void requestScheduling() {
        acquireReadLock();
        List<String> devices = dbManager.getAvailableDevices()
                .stream().map(MobileDeviceMeasurement::getDeviceId)
                .collect(Collectors.toList());
        if(devices.size() > 0){
            ConflictGraph graph = new ConflictGraph(activeJobs);
            List<Job> processedJobs = roundRobinAlgorithm.preprocessJobs(graph, devices);
            jobSchedule = roundRobinAlgorithm.generateSchedule(processedJobs,
                    graph.getConflictMatrix(), devices);
        } else{
            logger.error("Skipping scheduling as no devices have checked in recently");
        }
        releaseReadLock();
    }

    public List<MeasurementDescription> getActiveJobs(String deviceId) {
        acquireReadLock();
        List<MeasurementDescription> sentJobs = new ArrayList<>();
        if (jobSchedule.size() > 0) {
            ZonedDateTime currentTime = ZonedDateTime.now();
            for (Iterator<Map.Entry<Job, Assignment>> it = jobSchedule.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Job, Assignment> schedule = it.next();
                if (currentTime.isAfter(schedule.getValue().getDispatchTime()) &&
                        !schedule.getKey().isRemovable() &&
                        !schedule.getKey().isResettable(currentTime) &&
                        deviceId.equalsIgnoreCase(schedule.getValue().getDeviceKey())
                )
                    sentJobs.add(schedule.getKey().getMeasurementDescription());
                    it.remove();
            }
        }
        logger.info("Sent Jobs size is " + sentJobs.size());
        releaseReadLock();
        return sentJobs;
    }

    public Job recordSuccessfulJob(JSONObject jobDesc) {
        //assuming the JsonObj has key field mapping which measurement failed
        String key = jobDesc.getString("taskKey");
        //int instance = jobDesc.getInt("instance");
        for (Job job : activeJobs) {
            String currKey = job.getKey();
            if (currKey.equals(key) && jobDesc.getBoolean("success")) {
                logger.info("Job with key : " + currKey + "has been incremented by one");
                job.addNodeCount();
                if (job.nodesReached()) logger.info("\nJobs with Key " + key + " has Reached its Req Node count\n");
                return job;
            }
        }
        //false means the object is already removed since the count is reached
        return null;
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
