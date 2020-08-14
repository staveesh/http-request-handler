package com.taveeshsharma.httprequesthandler;

import com.taveeshsharma.orchestrator.JobTracker;
import com.taveeshsharma.orchestrator.Measurement;
import com.taveeshsharma.httprequesthandler.tcpserver.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
public class HttpRequestHandlerApplication {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(HttpRequestHandlerApplication.class, args);
	}

	@PostConstruct
	public void init(){
		Measurement.init();
		JobTracker.startJobTracker();
		TcpServer tcpServer = applicationContext.getBean(TcpServer.class);
		tcpServer.run();
	}
}