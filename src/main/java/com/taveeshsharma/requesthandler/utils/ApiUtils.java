package com.taveeshsharma.requesthandler.utils;

import com.taveeshsharma.requesthandler.dto.JobInterval;
import com.taveeshsharma.requesthandler.dto.documents.ScheduleRequest;
import org.apache.commons.math3.util.Precision;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ApiUtils {
    public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static ZonedDateTime addInterval(ZonedDateTime oldDate, JobInterval interval) { //interval is in hours
        return oldDate.plusHours(interval.getHours()).plusMinutes(interval.getMinutes()).plusSeconds(interval.getSeconds());
    }

    public static ZonedDateTime addMilliSeconds(ZonedDateTime oldDate, long milliseconds) {
        return oldDate.plus(milliseconds, ChronoUnit.MILLIS);
    }

    public static Optional<ApiError> isValidScheduleRequest(ScheduleRequest request) {
        boolean isValidRequestType = false;
        for (Constants.RequestType requestType : Constants.RequestType.values()) {
            if (requestType.name().equalsIgnoreCase(request.getRequestType())) {
                isValidRequestType = true;
                break;
            }
        }
        if (!isValidRequestType)
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API001.getErrorCode(),
                    ApiErrorCode.API001.getErrorMessage()
            ));

        boolean isValidMeasurementType = false;
        for (Constants.MeasurementType measurementType : Constants.MeasurementType.values()) {
            String type = request.getJobDescription().getMeasurementDescription().getType();
            if (measurementType.name().equalsIgnoreCase(type)) {
                isValidMeasurementType = true;
                break;
            }
        }
        if (!isValidMeasurementType)
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API002.getErrorCode(),
                    ApiErrorCode.API002.getErrorMessage()
            ));

        ZonedDateTime start = request.getJobDescription().getMeasurementDescription().getStartTime();
        ZonedDateTime end = request.getJobDescription().getMeasurementDescription().getEndTime();
        if (end.isBefore(start)) {
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API007.getErrorCode(),
                    ApiErrorCode.API007.getErrorMessage()
            ));
        }
        // If start time is before current time, throw error
        else if (start.isBefore(ZonedDateTime.now())) {
            return Optional.of(new ApiError(Constants.BAD_REQUEST,
                    ApiErrorCode.API008.getErrorCode(),
                    ApiErrorCode.API008.getErrorMessage()
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

    public static Double mean(List<Double> values) {
        int n = values.size();
        if (n == 0)
            return -1.0;
        Double sum = 0.0;
        sum += values.stream().reduce(0.0, Double::sum);
        return Precision.round(sum / n, 2);
    }

    public static Double median(List<Double> values) {
        int n = values.size();
        if (n == 0)
            return -1.0;
        Collections.sort(values);
        if (n % 2 == 0)
            return (values.get(n / 2 - 1) + values.get(n / 2)) / 2;
        return Precision.round(values.get(n / 2), 2);
    }

    public static Double stddev(List<Double> values, double avg) {
        int n = values.size();
        if (n == 0)
            return -1.0;
        double total = values.stream().reduce(0.0, (subtotal, val) -> subtotal + (val - avg) * (val - avg));
        return Precision.round(Math.sqrt(total / n), 2);
    }

    public static Double max(List<Double> values) {
        int n = values.size();
        if (n == 0)
            return -1.0;
        return Precision.round(values.stream().reduce(0.0, Math::max), 2);
    }
}
