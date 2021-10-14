package com.taveeshsharma.requesthandler.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.taveeshsharma.requesthandler.dto.DeviceSecurityConfig;
import com.taveeshsharma.requesthandler.dto.documents.Cipher;
import com.taveeshsharma.requesthandler.dto.documents.Filter;
import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import com.taveeshsharma.requesthandler.dto.documents.VpnServer;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.SchedulerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/job-result")
    public void processJobResult(@Payload String jobResult) {
        schedulerService.recordSuccessfulJob(new JSONObject(jobResult));
    }

    @MessageMapping("/summary-checkin")
    public void getLastSummaryCheckinTime(@Payload String deviceId){
        String timestamp = dbManager.findLastSummaryCheckinTime(deviceId);
        messagingTemplate.convertAndSendToUser(deviceId, "/queue/timestamp", timestamp);
    }

    @MessageMapping("/usage-summary")
    public void collectUsageSummary(@Payload String summary){
        final Gson builder = new GsonBuilder()
                                .registerTypeAdapter(Date.class,
                                (JsonDeserializer) (jsonElement, type1, context) -> new Date(jsonElement.getAsJsonPrimitive().getAsLong()))
                            .create();
        dbManager.writePersonalData(builder.fromJson(summary, PersonalData.class));
    }

    @MessageMapping("/security-config")
    public void getSecurityConfig(@Payload String payload){
        JSONObject params = new JSONObject(payload);
        String level = params.getString("level");
        String deviceId = params.getString("deviceId");
        String networkType = params.getString("networkType");
        Cipher cipher = dbManager.findBestCipherForLevel(level);
        Filter filter = dbManager.findBestFilter(networkType, level);
        VpnServer vpn = new VpnServer();
        if(level.equalsIgnoreCase("high"))
            vpn = dbManager.getBestVpnServer();
        DeviceSecurityConfig config = new DeviceSecurityConfig(filter, cipher, vpn);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            messagingTemplate.convertAndSendToUser(deviceId, "/queue/best-config", objectMapper.writeValueAsString(config));
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON");
        }
    }
}