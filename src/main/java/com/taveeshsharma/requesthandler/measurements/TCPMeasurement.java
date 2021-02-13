package com.taveeshsharma.requesthandler.measurements;


import com.taveeshsharma.requesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

/***
 * Class to store the TCP measurement we get.
 */
@Measurement(name= Constants.TCP_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class TCPMeasurement extends Measurements{

    @Column(name="tcp_speed_results")
    private String speedValues;

    @Column(name = "data_limit_exceeded")
    private boolean dataLimitExceeded;

    @Column(name = "duration")
    private Double measurementDuration;

    @Column(name = "mean_speed")
    private Double meanSpeed;

    @Column(name = "median_speed")
    private Double medianSpeed;

    @Column(name = "std_dev_speed")
    private Double stdDevSpeed;

    @Column(name = "max_speed")
    private Double maxSpeed;

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

    public Double getMeanSpeed() {
        return meanSpeed;
    }

    public void setMeanSpeed(Double meanSpeed) {
        this.meanSpeed = meanSpeed;
    }

    public Double getMedianSpeed() {
        return medianSpeed;
    }

    public void setMedianSpeed(Double medianSpeed) {
        this.medianSpeed = medianSpeed;
    }

    public Double getStdDevSpeed() {
        return stdDevSpeed;
    }

    public void setStdDevSpeed(Double stdDevSpeed) {
        this.stdDevSpeed = stdDevSpeed;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
