package com.taveeshsharma.requesthandler.measurements;

import com.taveeshsharma.requesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Measurement(name= Constants.ACCESS_POINT_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class AccessPointMeasurement {
    @Column(name = "time")
    private Instant time;
    @Column(name = "device_id")
    private String deviceId;
    @Column(name = "bssid")
    private String BSSID;
    @Column(name = "ssid")
    private String SSID;
    @Column(name = "frequency")
    private Integer frequency;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "link_speed")
    private Integer linkSpeed;
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "rssi")
    private Integer rssi;

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(Integer linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }
}
