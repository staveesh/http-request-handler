package com.taveeshsharma.requesthandler.tcpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.taveeshsharma.requesthandler.dto.documents.Job;
import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import com.taveeshsharma.requesthandler.orchestration.Measurement;
import com.taveeshsharma.requesthandler.orchestration.OrchAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ServerSocketHandler {

    @Autowired
    private DatabaseManager databaseManager;

    private void recordCheckinRequest(JSONObject request){
        JSONObject accessPoint = request.getJSONObject("accessPointInfo");
        databaseManager.writeAccessPointInfo(accessPoint);
        databaseManager.writeMobileDeviceInfo(request);
    }

    private static final Logger logger = LoggerFactory.getLogger(ServerSocketHandler.class);

    public String handleMessage(byte[] message, MessageHeaders messageHeaders) {
        String jsonString = new String(message);
        JSONObject request = encodeJSON(jsonString);
        if (request.has("requestType")) {
            String type = request.getString("requestType");
            if(type.equalsIgnoreCase("checkin")) {
                recordCheckinRequest(request);
                JSONArray jobArray = (JSONArray) OrchAPI.returnResponse(request);
                logger.info("Active Jobs Sent To Phone");
                return jobArray.toString();
            }else if(type.equalsIgnoreCase("summary")){
                logger.info(jsonString);
                final Gson builder = new GsonBuilder()
                        .registerTypeAdapter(Date.class, (JsonDeserializer) (jsonElement, type1, context) -> new Date(jsonElement.getAsJsonPrimitive().getAsLong()))
                        .create();
                databaseManager.writePersonalData(builder.fromJson(jsonString, PersonalData.class));
            }
            else if(type.equalsIgnoreCase("summary-checkin")){
                return databaseManager.findLastSummaryCheckinTime(request.getString("deviceId"));
            }
        } else{
            if(request.getBoolean("isExperiment")){
                Job job = Measurement.recordSuccessfulJob(request);
                databaseManager.upsertJob(job);
            }
            databaseManager.writeValues(request);
        }
        return "";
    }

    private JSONObject encodeJSON(String jsonString) {
        return new JSONObject(jsonString);
    }
}
