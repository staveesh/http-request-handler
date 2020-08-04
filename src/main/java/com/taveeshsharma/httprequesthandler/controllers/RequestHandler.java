package com.taveeshsharma.httprequesthandler.controllers;

import com.bugbusters.orchastrator.Measurement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taveeshsharma.httprequesthandler.utils.ApiError;
import com.taveeshsharma.httprequesthandler.utils.ApiUtils;
import com.taveeshsharma.httprequesthandler.dto.documents.ScheduleRequest;
import com.taveeshsharma.httprequesthandler.manager.DatabaseManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Autowired
    private DatabaseManager dbManager;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public ResponseEntity<?> scheduleMeasurement(@RequestBody ScheduleRequest request){
        logger.info("Received POST request for scheduling measurement : "+request);
        Optional<ApiError> error = ApiUtils.isValidScheduleRequest(request);
        if(error.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.get());
        dbManager.insertScheduledJob(request);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        JSONObject reqObject = new JSONObject(json);
        Measurement.addMeasurement(reqObject);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public ResponseEntity<?> getJobResults(@RequestParam(value = "id", required = false) String id,
                                           @RequestParam(value = "type") String type){
        logger.info(String.format(
                "Received GET request for retrieving jobs with id = %s and type = %s",
                id, type));
        JSONObject results = dbManager.getMeasurement(id, type);
        return ResponseEntity.ok().body(results);
    }

    @RequestMapping(value = "/results/jobs",method = RequestMethod.GET)
    public ResponseEntity<?> getJobDescription(@RequestParam("type") String type){
        logger.info(String.format(
                "Received GET request for retrieving job description with type = %s", type));
        List<ScheduleRequest> jobs = dbManager.getScheduledJobs(type.toUpperCase());
        return ResponseEntity.ok().body(jobs);
    }

    @RequestMapping(value = "/app-usage",method = RequestMethod.GET)
    public ResponseEntity<?> getAppUsage(@RequestParam("email") String email){
        logger.info(String.format(
                "Received GET request for retrieving app usage with email = %s", email));
        return ResponseEntity.ok().build();
    }
}
