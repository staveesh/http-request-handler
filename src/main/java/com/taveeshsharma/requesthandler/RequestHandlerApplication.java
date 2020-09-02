package com.taveeshsharma.requesthandler;

import com.taveeshsharma.requesthandler.tcpserver.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class RequestHandlerApplication {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(RequestHandlerApplication.class, args);
	}

	@PostConstruct
	public void init(){
		TcpServer tcpServer = applicationContext.getBean(TcpServer.class);
		tcpServer.run();
	}
}