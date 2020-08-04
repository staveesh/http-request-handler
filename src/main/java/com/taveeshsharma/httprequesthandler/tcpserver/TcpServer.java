package com.taveeshsharma.httprequesthandler.tcpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

@Service
public class TcpServer {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    @Value("${tcp.server.port}")
    private int port;

    @Autowired
    private ApplicationContext context;

    @Async
    public void run(){
        logger.info("TCP server is listening on port "+port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                TcpRequestHandler handler = context.getBean(TcpRequestHandler.class);
                handler.setServerSocket(serverSocket);
                handler.setClientSocket(serverSocket.accept());
                handler.run();
            }
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
