package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.dto.documents.JobMetrics;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.ZonedDateTime;
import java.util.*;

@Configuration
public class JobTrackerConfig implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(JobTrackerConfig.class);

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(jobTrackerThread());
        taskRegistrar.addTriggerTask(() -> {
            logger.info("Tracker thread is running...");
            schedulerService.acquireWriteLock();
            List<Job> activeJobs = schedulerService.getJobs();
            boolean schedulingRequired = false;
            ZonedDateTime currentTime = ZonedDateTime.now();
            for (Iterator<Job> iterator = activeJobs.iterator(); iterator.hasNext(); ) {
                Job job = iterator.next();
                if(job.isRemovable()){
                    String jobKey = job.getKey();
                    iterator.remove();
                    // Remove all instances of the job from tracker
                    Set<String> jobInstanceTracker = schedulerService.getJobInstanceTracker();
                    jobInstanceTracker.removeIf(instanceKey -> instanceKey.contains(jobKey + '-'));
                    logger.info("Job id with "+jobKey +" is removed");
                }
                else if(job.isResettable(currentTime)) {
                    job.reset();
                    JobMetrics metrics = new JobMetrics();
                    String jobKey = job.getKey();
                    int instanceNumber = job.getInstanceNumber().get();
                    metrics.setId(jobKey+"-"+instanceNumber);
                    metrics.setInstanceNumber(instanceNumber);
                    metrics.setJobKey(jobKey);
                    metrics.setAddedToQueueAt(ZonedDateTime.now());
                    dbManager.upsertJob(job);
                    dbManager.upsertJobMetrics(metrics);
                    logger.info("Job id with " + job.getKey() + " is reset");
                }
                if (currentTime.isAfter(job.getStartTime())) {
                    schedulingRequired = true;
                }
            }
            if (schedulingRequired) {
                Schedule schedule = schedulerService.requestScheduling(null, null);
                schedulerService.sendActiveJobs(schedule);
            }
            schedulerService.releaseWriteLock();
        }, triggerContext -> {
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            Calendar nextExecutionTime = new GregorianCalendar();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.MILLISECOND, (int) Constants.JOB_TRACKER_PERIOD_SECONDS*1000);
            return nextExecutionTime.getTime();
        });
    }

    @Bean
    public TaskScheduler jobTrackerThread() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("JobTracker");
        scheduler.setPoolSize(100);
        scheduler.initialize();
        return scheduler;
    }
}
