package com.taveeshsharma.httprequesthandler.utils;

import com.bugbusters.orchastrator.Utils;
import com.taveeshsharma.httprequesthandler.dto.documents.ScheduleRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Optional;

public class ApiUtils {
    public static Optional<ApiError> isValidScheduleRequest(ScheduleRequest request){
        boolean isValidRequestType = false;
        for(Constants.RequestType requestType: Constants.RequestType.values()){
            if(requestType.name().equalsIgnoreCase(request.getRequestType())) {
                isValidRequestType = true;
                break;
            }
        }
        if(!isValidRequestType)
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API001.getErrorCode(),
                    ApiErrorCode.API001.getErrorMessage()
            ));

        boolean isValidMeasurementType = false;
        for(Constants.MeasurementType measurementType: Constants.MeasurementType.values()){
            String type = request.getJobDescription().getMeasurementDescription().getType();
            if(measurementType.name().equalsIgnoreCase(type)) {
                isValidMeasurementType = true;
                break;
            }
        }
        if(!isValidMeasurementType)
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API002.getErrorCode(),
                    ApiErrorCode.API002.getErrorMessage()
            ));

        String startTime = request.getJobDescription().getMeasurementDescription().getStartTime();
        String endTime = request.getJobDescription().getMeasurementDescription().getEndTime();
        try{
            Utils.getDate(startTime);
            Utils.getDate(endTime);
        } catch (ParseException e){
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API003.getErrorCode(),
                    ApiErrorCode.API003.getErrorMessage()
            ));
        }
        return Optional.empty();
    }

    public static String hashUserName(String userName) {
        if (userName.equals("Anonymous")) {
            return userName;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashInBytes = md.digest(userName.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
