package com.taveeshsharma.requesthandler.orchestration;

import com.taveeshsharma.requesthandler.dto.JobDescription;
import org.json.JSONArray;
import org.json.JSONObject;

public class OrchAPI {

    private final static String MEASUREMENT_SCHEDULE="SCHEDULE_MEASUREMENT";
    private final static String MEASUREMENT_SUCCESSFUL="MEASUREMENT_SUCCESSFUL";
    private final static String MEASUREMENT_CHECK_IN_TYPE="CHECKIN";

    public static Object returnResponse(JSONObject request){
       String requestType = (String)request.get("requestType");
       Object response=null;
       if(requestType.equalsIgnoreCase(MEASUREMENT_CHECK_IN_TYPE)){
           //send the client a list of available jobs;
           response= new JSONArray(Measurement.getActiveJobs());
           return response;
       }
       else if(requestType.equals(MEASUREMENT_SUCCESSFUL)) {
           return Measurement.recordSuccessfulJob(request);
       }
       else{
           throw new IllegalArgumentException();
       }
    }
}
