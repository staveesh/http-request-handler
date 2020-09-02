package com.taveeshsharma.requesthandler.orchestration;

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
           response= Measurement.getActiveJobs();
           return response;
       }
       else if(requestType.equals(MEASUREMENT_SCHEDULE)){
            //the request contains Measurement Description so should be added to the list of jobs
            Measurement.addMeasurement(request);
       }
       else if(requestType.equals(MEASUREMENT_SUCCESSFUL)) {
           return Measurement.recordSuccessfulJob(request);
       }
       else{
           throw new IllegalArgumentException();
       }
       return response;
    }
}
