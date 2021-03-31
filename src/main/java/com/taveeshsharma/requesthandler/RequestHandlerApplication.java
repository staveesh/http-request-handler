package com.taveeshsharma.requesthandler;

import com.taveeshsharma.requesthandler.analyzer.Driver;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.SchedulerService;
import com.taveeshsharma.requesthandler.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class RequestHandlerApplication {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerApplication.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SchedulerService schedulerService;

    public static void main(String[] args) {
        SpringApplication.run(RequestHandlerApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Initialize the job queue
        List<Job> storedActiveJobs = dbManager.getCurrentlyActiveJobs(new Date());
        for (Job job : storedActiveJobs) {
            logger.info(job.getKey() + " : " + job.getStartTime().withZoneSameInstant(ZoneId.systemDefault()) +
                    " : " + job.getEndTime().withZoneSameInstant(ZoneId.systemDefault()));
            schedulerService.addMeasurement(job);
        }
        // Initiate PCAP File analyzer
        Driver pcapAnalyzerDriver = applicationContext.getBean(Driver.class);
        pcapAnalyzerDriver.initiate();
    }
}