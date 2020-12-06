package com.taveeshsharma.requesthandler.orchestration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JobTracker {

    private static final Logger logger = LoggerFactory.getLogger(JobTracker.class);

    @Scheduled(fixedRate = 2*60*1000, initialDelay = 60*1000)
    public void track(){
        Measurement.acquireWriteLock();
        logger.info("Job tracking is being perfomed");
        List<Job> activeJobs= Measurement.getJobs();
        //TODO synchronize appropriately as well as how often does the thread check
        //if end time is reached remove job
        //if its a recurring job once
        //loop backwards so as to avoid skipping an index if I remove an element
        Date currentTime = new Date();
        for(int i=activeJobs.size()-1;i>=0;i--){
            Job job=activeJobs.get(i);
            if(job.isRemovable()){
                activeJobs.remove(i);
                logger.info("Job is with "+job.getMeasurementDesc().get("key") +" removed");
            }
            else if(job.isResettable(currentTime)){
                job.reset();
                logger.info("Job is with "+job.getMeasurementDesc().get("key") +" is reset");
            }
        }
        logger.info("Current Job Size is " + activeJobs.size());
        Measurement.releaseWriteLock();
        logger.info("Job Tracker has Finished");
    }
}
