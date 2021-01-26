package com.taveeshsharma.requesthandler;

import com.taveeshsharma.requesthandler.analyzer.Driver;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.Measurement;
import com.taveeshsharma.requesthandler.tcpserver.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class RequestHandlerApplication {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DatabaseManager dbManager;

	public static void main(String[] args) {
		SpringApplication.run(RequestHandlerApplication.class, args);
	}

	@PostConstruct
	public void init(){
		// Run TCP server
		TcpServer tcpServer = applicationContext.getBean(TcpServer.class);
		tcpServer.run();
		// Initialize the job queue
		List<Job> storedActiveJobs = dbManager.getCurrentlyActiveJobs(new Date());
		for(Job job : storedActiveJobs)
			Measurement.addMeasurement(job);
		// Initiate PCAP File analyzer
		Driver pcapAnalyzerDriver = applicationContext.getBean(Driver.class);
		pcapAnalyzerDriver.initiate();
	}
}