package com.taveeshsharma.requesthandler.analyzer;

import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import io.pkts.PacketHandler;
import io.pkts.Pcap;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public class Driver {

    private static final Logger logger = LoggerFactory.getLogger(Driver.class);

    private FileMonitor fileMonitor;

    @Value("${file.server.hostname}")
    private String fileServerHostname;
    @Value("${num.retry.connect}")
    private int numRetryConnect;
    @Value("${milliseconds.till.retry.connect}")
    private int millisecondsTillRetryConnect;
    @Value("${file.monitor.delay}")
    private int fileMonitorDelay;

    @Autowired
    private DatabaseManager dbManager;

    private boolean connectionEstablished = false;

    @Async(value = "applicationTaskExecutor")
    public void initiate(){
        if(!fileServerHostname.isEmpty()) {
            this.fileMonitor = new FileMonitor(
                    fileServerHostname,
                    numRetryConnect,
                    millisecondsTillRetryConnect,
                    fileMonitorDelay
            );
            this.connectionEstablished = fileMonitor.start();
        }
    }

    @Scheduled(fixedRateString = "${milliseconds.till.analyze.pcapfiles}",initialDelayString = "${milliseconds.init.delay}")
    public void analyze(){
        logger.info("Analyzing PCAP Files...");
        if(this.connectionEstablished){
            Set<FileObject> pcapFiles = fileMonitor.getCreatedFiles();
            PacketHandler handler = new PacketHandlerImpl(dbManager);
            for (FileObject f : pcapFiles) {
                try {
                    processPcapFile(Pcap.openStream(f.getContent().getInputStream()), handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileMonitor.doneProcessing();
            ((PacketHandlerImpl)handler).clearList();
        }
        else{
            logger.error("Please check the host in the properties file and restart the service");
        }
    }

    private void processPcapFile(Pcap pcap, PacketHandler handler) {
        try {
            pcap.loop(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
