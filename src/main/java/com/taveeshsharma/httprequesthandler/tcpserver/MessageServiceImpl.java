package com.taveeshsharma.httprequesthandler.tcpserver;

import com.bugbusters.orchastrator.Measurement;
import com.bugbusters.orchastrator.OrchAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public byte[] processMessage(byte[] tcpRequest) {
        String messageContent = new String(tcpRequest);
        JSONObject request = new JSONObject(messageContent);
        if (request.has("request_type")) {
            String type = request.getString("request_type");
            if(type.equalsIgnoreCase("checkin")) {
                JSONArray jobArray = (JSONArray) OrchAPI.returnResponse(request);
                LOGGER.info("Sending active jobs To Phone");
                return (jobArray.toString()).getBytes();
            }else if(type.equalsIgnoreCase("summary")){
//                DatabaseManager.writePersonalData(messageContent);
            }
        } else{
            if(request.getBoolean("is_experiment")){
                Measurement.recordSuccessfulJob(request);
            }
//            DatabaseManager.writeValues(request);
        }
        return new byte[1024];
    }
}
