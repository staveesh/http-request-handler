package com.taveeshsharma.requesthandler.measurements;

import com.taveeshsharma.requesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

@Measurement(name = Constants.VIDEO_TEST_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class VideoTestMeasurement extends Measurements{
    @Column(name = "buffer")
    private Double buffer;
    @Column(name = "loadTime")
    private Double loadTime;
    @Column(name = "bandwidth")
    private Double bandwidth;

    public Double getBuffer() {
        return buffer;
    }

    public void setBuffer(Double buffer) {
        this.buffer = buffer;
    }

    public Double getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Double loadTime) {
        this.loadTime = loadTime;
    }

    public Double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Double bandwidth) {
        this.bandwidth = bandwidth;
    }
}
