package com.taveeshsharma.requesthandler.measurements;

import com.taveeshsharma.requesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

@Measurement(name = Constants.SPEED_TEST_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class SpeedTestMeasurement extends Measurements{
    @Column(name = "ping")
    private Double ping;
    @Column(name = "upload")
    private Double upload;
    @Column(name = "download")
    private Double download;

    public Double getPing() {
        return ping;
    }

    public void setPing(Double ping) {
        this.ping = ping;
    }

    public Double getUpload() {
        return upload;
    }

    public void setUpload(Double upload) {
        this.upload = upload;
    }

    public Double getDownload() {
        return download;
    }

    public void setDownload(Double download) {
        this.download = download;
    }
}
