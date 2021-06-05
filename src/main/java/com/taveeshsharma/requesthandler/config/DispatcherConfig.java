package com.taveeshsharma.requesthandler.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taveeshsharma.requesthandler.dto.MeasurementDescription;
import com.taveeshsharma.requesthandler.orchestration.DynamicScheduledTask;
import com.taveeshsharma.requesthandler.orchestration.JobDispatcher;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Configuration
public class DispatcherConfig implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(DispatcherConfig.class);

    @Autowired
    private JobDispatcher jobDispatcher;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Date nextRunTimeStamp;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(jobDispatcherThread());
        taskRegistrar.addTriggerTask(() -> {
            logger.info("Dispatcher thread is running...");
            jobDispatcher.acquireWriteLock();
            Set<DynamicScheduledTask> tasks = jobDispatcher.getTasks();
            logger.info("Jobs in Dispatcher : "+tasks.stream().map(task ->
                    task.getJob().getKey()+"-"+task.getJob().getInstanceNumber().get() + ":" + task.getDispatchTime())
                    .collect(Collectors.toList()));
            Iterator<DynamicScheduledTask> iter = tasks.iterator();
            List<DynamicScheduledTask> tasksToRun = new ArrayList<>();
            ZonedDateTime nextRun = ZonedDateTime.now().plusSeconds(Constants.JOB_DISPATCHER_PERIOD_SECONDS);
            nextRunTimeStamp = Date.from(nextRun.toInstant());
            while(iter.hasNext()){
                DynamicScheduledTask task = iter.next();
                Date taskDispatch = Date.from(task.getDispatchTime().toInstant());
                if(ZonedDateTime.now().isAfter(task.getDispatchTime())){
                    tasksToRun.add(task);
                    iter.remove();
                } else{
                    nextRunTimeStamp = taskDispatch;
                    break;
                }
            }
            jobDispatcher.releaseWriteLock();
            logger.info("Jobs going out now : "+tasksToRun.stream().map(task ->
                    task.getJob().getKey() + ":" + task.getDispatchTime())
                    .collect(Collectors.toList()));
            logger.info("Job dispatcher's next run : " + nextRunTimeStamp);
            Map<String, List<MeasurementDescription>> jobMap = new HashMap<>();
            for (DynamicScheduledTask task : tasksToRun) {
                if (!jobMap.containsKey(task.getDeviceId()))
                    jobMap.put(task.getDeviceId(), new ArrayList<>());
                jobMap.get(task.getDeviceId()).add(task.getJob().getMeasurementDescription());
            }
            for (Map.Entry<String, List<MeasurementDescription>> deviceJobs : jobMap.entrySet()) {
                List<MeasurementDescription> jobs = deviceJobs.getValue();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                try {
                    JSONArray jobsArray = new JSONArray(objectMapper.writeValueAsString(jobs));
                    for (int i = 0; i < jobsArray.length(); i++) {
                        JSONObject jobObject = jobsArray.getJSONObject(i);
                        logger.info("Dispatching job : "+jobObject.getString("key")+" to device : "+deviceJobs.getKey());
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
        }, triggerContext -> {
            if (nextRunTimeStamp == null)
                nextRunTimeStamp = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Constants.JOB_DISPATCHER_PERIOD_SECONDS));
            return nextRunTimeStamp;
        });
    }

    @Bean
    public TaskScheduler jobDispatcherThread() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("JobDispatcher");
        scheduler.setPoolSize(100);
        scheduler.initialize();
        return scheduler;
    }
}
