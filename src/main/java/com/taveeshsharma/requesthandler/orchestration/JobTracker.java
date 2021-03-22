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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
        ZonedDateTime currentTime = ZonedDateTime.now();
        boolean schedulingRequired = false;
        for(int i=activeJobs.size()-1;i>=0;i--){
            Job job=activeJobs.get(i);
            logger.info("Tracking "+job.getKey()+", start time = "+job.getStartTime().withZoneSameInstant(ZoneId.systemDefault()));
            logger.info("Minutes to start : "+ChronoUnit.MINUTES.between(currentTime, job.getStartTime()));
            if(job.isRemovable()){
                activeJobs.remove(i);
                logger.info("Job id with "+job.getKey() +" removed");
                schedulingRequired = true;
            }
            else if(job.isResettable(currentTime)) {
                job.reset();
                dbManager.upsertJob(job);
                logger.info("Job id with " + job.getKey() + " is reset");
                schedulingRequired = true;
            }
        }
        if(schedulingRequired && activeJobs.size() > 0)
            schedulerService.requestScheduling(null, null);
        logger.info("Current Job Size is " + activeJobs.size());
        schedulerService.releaseWriteLock();
        logger.info("Job Tracker has Finished");
    }
}
