package com.taveeshsharma.requesthandler.utils;

import com.taveeshsharma.requesthandler.controllers.RequestHandler;
import com.taveeshsharma.requesthandler.dto.documents.ScheduleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ApiUtils {
    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);
    private final static DateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private final static long HOURS=3600*1000; //since the intervals are in hours

    public static Date getDate(String date) throws ParseException
    {
        Date result=null;
        result = dateFormat.parse(date);
        return result;
    }

    public static String formatDate(Date date){
        return dateFormat.format(date);
    }
    public static Date addHours(Date oldDate,int interval){ //interval is in hours
        return new Date(oldDate.getTime()+(interval*HOURS));
    }

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
            Date start = getDate(startTime);
            Date end = getDate(endTime);
            logger.info("Start time = "+start+" end time = "+end);
            if(end.before(start)){
                return Optional.of(new ApiError(Constants.BAD_REQUEST,
                        ApiErrorCode.API007.getErrorCode(),
                        ApiErrorCode.API007.getErrorMessage()
                ));
            }
            // If start time is before current time, throw error
            else if(start.before(new Date())){
                return Optional.of(new ApiError(Constants.BAD_REQUEST,
                        ApiErrorCode.API008.getErrorCode(),
                        ApiErrorCode.API008.getErrorMessage()
                ));
            }
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
