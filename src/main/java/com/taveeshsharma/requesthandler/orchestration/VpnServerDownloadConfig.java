package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.documents.VpnServer;
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
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Configuration
public class VpnServerDownloadConfig implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(VpnServerDownloadConfig.class);

    @Autowired
    private DatabaseManager dbManager;

    private int lastAdded;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(downloaderThread());
        taskRegistrar.addTriggerTask(() -> {
            logger.info("VPN server downloader is running...");
            RestTemplate restTemplate = new RestTemplate();
            String servers = restTemplate.getForObject(Constants.VPN_SERVER_DOWNLOAD_URL, String.class);
            if(servers != null) {
                String[] lines = servers.split("\\r?\\n");
                int counter = -2;
                List<VpnServer> vpnServers = new ArrayList<>();
                for(String line : lines){
                    if(counter >= 0){
                        String[] contents = line.split("[,]");
                        if(contents.length == 15) {
                            VpnServer server = new VpnServer();
                            server.setHostName(contents[0]);
                            server.setIp(contents[1]);
                            server.setScore(Long.parseLong(contents[2]));
                            server.setPing(Integer.parseInt(contents[3]));
                            server.setSpeed(Long.parseLong(contents[4]));
                            server.setCountry(contents[5]);
                            server.setCountryCode(contents[6]);
                            server.setNumVpnSessions(Integer.parseInt(contents[7]));
                            server.setUptime(Long.parseLong(contents[8]));
                            server.setTotalUsers(Long.parseLong(contents[9]));
                            server.setTotalTraffic(Long.parseLong(contents[10]));
                            server.setLogType(contents[11]);
                            server.setOperator(contents[12]);
                            server.setMessage(contents[13]);
                            server.setConfigData(contents[14]);
                            vpnServers.add(server);
                        }
                    }
                    counter++;
                }
                lastAdded = counter;
                dbManager.updateVpnServers(vpnServers);
            }
        }, triggerContext -> {
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            Calendar nextExecutionTime = new GregorianCalendar();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            if(lastAdded > 0)
                nextExecutionTime.add(Calendar.MILLISECOND, (int) Constants.VPN_SERVER_DOWNLOADER_PERIOD_SECONDS*1000);
            else
                nextExecutionTime.add(Calendar.MILLISECOND, 40000);
            return nextExecutionTime.getTime();
        });
    }

    @Bean
    public TaskScheduler downloaderThread() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("vpnDownloader");
        scheduler.setPoolSize(100);
        scheduler.initialize();
        return scheduler;
    }
}
