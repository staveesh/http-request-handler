package com.taveeshsharma.httprequesthandler;

import com.bugbusters.orchastrator.JobTracker;
import com.bugbusters.orchastrator.Measurement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class HttpRequestHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpRequestHandlerApplication.class, args);
	}

	@PostConstruct
	public void startJobTracker(){
		Measurement.init();
		JobTracker.startJobTracker();
	}
}