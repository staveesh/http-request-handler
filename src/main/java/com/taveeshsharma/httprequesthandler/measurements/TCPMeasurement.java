package com.taveeshsharma.httprequesthandler.measurements;


import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

/***
 * Class to store the TCP measurement we get.
 */
@Measurement(name="tcp_speed_test", timeUnit = TimeUnit.MILLISECONDS)
public class TCPMeasurement extends Measurements{

    @Column(name="tcp_speed_results")
    private String speedValues;

    @Column(name = "data_limit_exceeded")
    private boolean dataLimitExceeded;

    @Column(name = "duration")
    private Double measurementDuration;

    public String getSpeedValues() {
        return speedValues;
    }

    public void setSpeedValues(String speedValues) {
        this.speedValues = speedValues;
    }

    public boolean isDataLimitExceeded() {
        return dataLimitExceeded;
    }

    public void setDataLimitExceeded(boolean dataLimitExceeded) {
        this.dataLimitExceeded = dataLimitExceeded;
    }

    public Double getMeasurementDuration() {
        return measurementDuration;
    }

    public void setMeasurementDuration(Double measurementDuration) {
        this.measurementDuration = measurementDuration;
    }
}
