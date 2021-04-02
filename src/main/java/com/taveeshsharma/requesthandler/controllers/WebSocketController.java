package com.taveeshsharma.requesthandler.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.SchedulerService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WebSocketController {

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
}