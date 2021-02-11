package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.measurements.MobileDeviceMeasurement;
import com.taveeshsharma.requesthandler.orchestration.algorithms.SchedulingAlgorithm;
import com.taveeshsharma.requesthandler.utils.ApiUtils;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobTracker {

    private static final Logger logger = LoggerFactory.getLogger(JobTracker.class);

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SchedulerService schedulerService;

    @Scheduled(fixedRate = 2*60*1000, initialDelay = 60*1000)
    public void track(){
        schedulerService.acquireWriteLock();
        logger.info("Job tracking is being performed");
        List<Job> activeJobs= schedulerService.getJobs();
        //TODO synchronize appropriately as well as how often does the thread check
        //if end time is reached remove job
        //if its a recurring job once
        //loop backwards so as to avoid skipping an index if I remove an element
        Date currentTime = new Date();
        Date minutesAfter = ApiUtils.addMinutes(currentTime, Constants.MINUTES_BEFORE_SCHEDULING_REQUEST);
        logger.info("minutesAfter = "+minutesAfter);
        List<Job> jobQueue = new ArrayList<>();
        for(int i=activeJobs.size()-1;i>=0;i--){
            Job job=activeJobs.get(i);
            logger.info("Tracking "+job.getKey()+", start time = "+job.getStartTime());
            if(job.isRemovable()){
                activeJobs.remove(i);
                logger.info("Job id with "+job.getKey() +" removed");
            }
            else if(job.isResettable(currentTime)) {
                job.reset();
                dbManager.upsertJob(job);
                logger.info("Job id with " + job.getKey() + " is reset");
            }
            else if(minutesAfter.after(job.getStartTime())){
                jobQueue.add(job);
            }
        }
        if(jobQueue.size() > 0){
            List<MobileDeviceMeasurement> data = dbManager.getAvailableDevices();
            List<String> deviceIds = data.stream()
                    .map(MobileDeviceMeasurement::getDeviceId)
                    .distinct()
                    .collect(Collectors.toList());
            logger.info("Devices that have checked-in recently : "+deviceIds);
            if(deviceIds.size() > 0)
                schedulerService.requestScheduling(jobQueue, deviceIds);
            else
                logger.info("Skipping scheduling as no devices are available");
        }
        logger.info("Current Job Size is " + activeJobs.size());
        schedulerService.releaseWriteLock();
        logger.info("Job Tracker has Finished");
    }
}
