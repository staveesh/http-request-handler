package com.taveeshsharma.httprequesthandler.controllers;

import com.taveeshsharma.httprequesthandler.dto.ScheduleRequest;
import com.taveeshsharma.httprequesthandler.repository.ScheduleRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Autowired
    private ScheduleRequestRepository scheduleRequestRepository;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public ResponseEntity<?> scheduleMeasurement(@RequestBody ScheduleRequest request){
        logger.info("Received POST request for scheduling measurement : "+request);
        // TODO: Add validations here
        scheduleRequestRepository.insert(request);
        // TODO: Add measurement to scheduled jobs here
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public ResponseEntity<?> getJobResults(@RequestParam("id") String id,
                                           @RequestParam("type") String type){
        logger.info(String.format(
                "Received GET request for retrieving jobs with id = %s and type = %s",
                id, type));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/results/jobs",method = RequestMethod.GET)
    public ResponseEntity<?> getJobDescription(@RequestParam("type") String type){
        logger.info(String.format(
                "Received GET request for retrieving job description with type = %s", type));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/app-usage",method = RequestMethod.GET)
    public ResponseEntity<?> getAppUsage(@RequestParam("email") String email){
        logger.info(String.format(
                "Received GET request for retrieving app usage with email = %s", email));
        return ResponseEntity.ok().build();
    }
}
