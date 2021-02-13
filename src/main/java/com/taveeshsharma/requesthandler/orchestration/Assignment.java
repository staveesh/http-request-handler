package com.taveeshsharma.requesthandler.orchestration;

import java.time.ZonedDateTime;
import java.util.Date;

public class Assignment {
    private ZonedDateTime dispatchTime;
    private String deviceKey;

    public Assignment(ZonedDateTime dispatchTime, String deviceKey) {
        this.dispatchTime = dispatchTime;
        this.deviceKey = deviceKey;
    }

    public ZonedDateTime getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(ZonedDateTime dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }
}
