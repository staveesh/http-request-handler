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
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

@Service
@Scope("prototype")
public class TcpRequestHandler {

    private static Logger logger = LoggerFactory.getLogger(TcpRequestHandler.class);

    private ServerSocket serverSocket;
    private Socket clientSocket;

    @Autowired
    private DatabaseManager databaseManager;

    @Async
    public void run() {
        try {
            Scanner in = new Scanner(clientSocket.getInputStream());
            String jsonString;
            while (true) {
                if (in.hasNextLine()) {
                    jsonString = in.nextLine();
                    JSONObject request = encodeJSON(jsonString);
                    if (request.has("requestType")) {
                        String type = request.getString("requestType");
                        if(type.equalsIgnoreCase("checkin")) {
                            JSONArray jobArray = (JSONArray) OrchAPI.returnResponse(request);
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                            out.println(jobArray.toString());
                            out.flush();
                            logger.info("Active Jobs Sent To Phone");
                        }else if(type.equalsIgnoreCase("summary")){
                            final Gson builder = new GsonBuilder()
                                    .registerTypeAdapter(Date.class, (JsonDeserializer) (jsonElement, type1, context) -> new Date(jsonElement.getAsJsonPrimitive().getAsLong()))
                                    .create();
                            databaseManager.writePersonalData(builder.fromJson(jsonString, PersonalData.class));
                        }
                    } else{
                        if(request.getBoolean("isExperiment")){
                          	 Job job = Measurement.recordSuccessfulJob(request);
                          	 databaseManager.upsertJob(job);
                        }
                        databaseManager.writeValues(request);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject encodeJSON(String jsonString) {
        JSONObject request = new JSONObject(jsonString);
        return request;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
