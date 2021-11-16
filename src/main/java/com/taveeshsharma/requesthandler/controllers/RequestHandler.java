package com.taveeshsharma.requesthandler.controllers;

import com.taveeshsharma.requesthandler.dto.AuthenticationResponse;
import com.taveeshsharma.requesthandler.dto.NodesResponse;
import com.taveeshsharma.requesthandler.dto.documents.*;
import com.taveeshsharma.requesthandler.manager.UserManager;
import com.taveeshsharma.requesthandler.orchestration.SchedulerService;
import com.taveeshsharma.requesthandler.utils.*;
import com.taveeshsharma.requesthandler.manager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// TODO: Proper error handling in login/signup endpoints

@RestController
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SchedulerService schedulerService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> addNewUser(@RequestBody User user){
        user.setUserName(ApiUtils.hashUserName(user.getUserName()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try{
            UserDetails userDetails = userManager.loadUserByUsername(user.getUserName());
            // If user is found, it already exists with the provided username
            ApiError error = new ApiError(
                    Constants.BAD_REQUEST,
                    ApiErrorCode.API004.getErrorCode(),
                    ApiErrorCode.API004.getErrorMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (UsernameNotFoundException e){
            userManager.save(user);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) throws Exception{
        user.setUserName(ApiUtils.hashUserName(user.getUserName()));
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiError(
                            Constants.UNAUTHORIZED,
                            ApiErrorCode.API006.getErrorCode(),
                            ApiErrorCode.API006.getErrorMessage()
                    )
            );
        }
        final UserDetails userDetails = userManager.loadUserByUsername(user.getUserName());
        // Check if supplied roles match stored roles
        Set<String> storedRoles = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Set<String> suppliedRoles = user.getRoles()
                .stream().map(UserRole::getRole).collect(Collectors.toSet());
        if(!storedRoles.equals(suppliedRoles)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiError(
                            Constants.BAD_REQUEST,
                            ApiErrorCode.API005.getErrorCode(),
                            ApiErrorCode.API005.getErrorMessage()
                    )
            );
        }
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public ResponseEntity<?> scheduleMeasurement(@RequestBody ScheduleRequest request){
        request.setUserId(ApiUtils.hashUserName(request.getUserId()));
        Optional<ApiError> error = ApiUtils.isValidScheduleRequest(request);
        if(error.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.get());
        dbManager.insertScheduledJob(request);
        Job newJob = new Job(request.getJobDescription());
        dbManager.upsertJob(newJob);
        schedulerService.addMeasurement(newJob);
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
}
