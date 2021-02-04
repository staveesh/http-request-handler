package com.taveeshsharma.requesthandler.orchestration;

import java.util.Date;

public class Assignment {
    private Date dispatchTime;
    private String deviceKey;

    public Assignment(Date dispatchTime, String deviceKey) {
        this.dispatchTime = dispatchTime;
        this.deviceKey = deviceKey;
    }

    public Date getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(Date dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }
}
