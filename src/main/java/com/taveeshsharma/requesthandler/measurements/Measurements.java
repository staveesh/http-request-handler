package com.taveeshsharma.requesthandler.measurements;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

/**
 * This is the base measurement class and contains all the column fields that each measurement should have.
 * All the other network measurement extend from this class.
 */
// TODO: Add boolean flag "success"
@Measurement(name="SuperMeasurement")
public class Measurements {
    @Column(name = "time")
    private Instant time;

    @Column(name = "deviceId")
    private String deviceId;

    @Column(name="taskKey", tag = true)
    private String taskKey;

    @Column(name = "username")
    private String userName;

    @Column(name="target")
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskKey() {
        return taskKey;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
