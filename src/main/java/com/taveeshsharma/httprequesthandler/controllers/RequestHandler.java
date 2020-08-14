package com.taveeshsharma.httprequesthandler.controllers;

import com.taveeshsharma.orchestrator.Measurement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taveeshsharma.httprequesthandler.dto.AppNetworkUsage;
import com.taveeshsharma.httprequesthandler.dto.TotalAppUsage;
import com.taveeshsharma.httprequesthandler.dto.documents.PersonalData;
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

import java.math.BigDecimal;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        return ResponseEntity.ok().body(dbManager.getMeasurement(id, type));
    }

    @RequestMapping(value = "/results/jobs",method = RequestMethod.GET)
    public ResponseEntity<?> getJobDescription(@RequestParam("type") String type){
        logger.info(String.format(
                "Received GET request for retrieving job description with type = %s", type));
        List<ScheduleRequest> jobs = dbManager.getScheduledJobs(type);
        return ResponseEntity.ok().body(jobs);
    }

    @RequestMapping(value = "/app-usage",method = RequestMethod.GET)
    public ResponseEntity<?> getAppUsage(@RequestParam("email") String email){
        logger.info(String.format(
                "Received GET request for retrieving app usage with email = %s", email));
        List<PersonalData> appUsage = dbManager.readPersonalData(email);
        // Aggregate to return overall usage in MB
        Map<String, TotalAppUsage> aggregated = new HashMap<>();
        for(PersonalData data : appUsage){
            List<AppNetworkUsage> allAppsSummary = data.getUserSummary();
            for(AppNetworkUsage appSummary : allAppsSummary){
                if(!aggregated.containsKey(appSummary.getName())){
                    TotalAppUsage totalAppUsage = new TotalAppUsage();
                    totalAppUsage.setName(appSummary.getName());
                    totalAppUsage.setRx(BigDecimal.ZERO);
                    totalAppUsage.setTx(BigDecimal.ZERO);
                    aggregated.put(appSummary.getName(), totalAppUsage);
                }
                else{
                    TotalAppUsage totalAppUsage = aggregated.get(appSummary.getName());
                    totalAppUsage.setRx(totalAppUsage.getRx()
                            .add(BigDecimal.valueOf((double) appSummary.getRx() / (1024 * 1024))));
                    totalAppUsage.setTx(totalAppUsage.getTx()
                            .add(BigDecimal.valueOf((double) appSummary.getTx() / (1024 * 1024))));
                }
            }
        }
        List<TotalAppUsage> result = new ArrayList<>(aggregated.values());
        return ResponseEntity.ok().body(result);
    }
}
