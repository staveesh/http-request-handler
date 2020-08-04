package com.taveeshsharma.httprequesthandler.measurements;


import com.taveeshsharma.httprequesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

/**
 * Class resembling the important fields from a ping measurement results.
 */
@Measurement(name = Constants.PING_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class PingMeasurement extends Measurements{
    @Column(name ="target_ip ")
    private String  targetIpAddress;

    @Column(name ="ping_method ")
    private String pingMethod;

    @Column(name = "mean_rtt_ms")
    private Double meanRttMS;

    @Column(name = "max_rtt_ms")
    private Double maxRttMs;

    @Column(name = "stddev_rtt_ms")
    private Double stddevRttMs;

    public String getTargetIpAddress() {
        return targetIpAddress;
    }

    public void setTargetIpAddress(String targetIpAddress) {
        this.targetIpAddress = targetIpAddress;
    }

    public String getPingMethod() {
        return pingMethod;
    }

    public void setPingMethod(String pingMethod) {
        this.pingMethod = pingMethod;
    }

    public Double getMeanRttMS() {
        return meanRttMS;
    }

    public void setMeanRttMS(Double meanRttMS) {
        this.meanRttMS = meanRttMS;
    }

    public Double getMaxRttMs() {
        return maxRttMs;
    }

    public void setMaxRttMs(Double maxRttMs) {
        this.maxRttMs = maxRttMs;
    }

    public Double getStddevRttMs() {
        return stddevRttMs;
    }

    public void setStddevRttMs(Double stddevRttMs) {
        this.stddevRttMs = stddevRttMs;
    }
}
