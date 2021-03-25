package com.taveeshsharma.requesthandler.dto.documents;

import com.taveeshsharma.requesthandler.dto.AppNetworkUsage;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("personal")
public class PersonalData {
    private String requestType;
    private String institution;
    private String deviceId;
    private Date startTime;
    private Date endTime;
    private List<AppNetworkUsage> wifiSummary;
    private List<AppNetworkUsage> mobileSummary;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<AppNetworkUsage> getWifiSummary() {
        return wifiSummary;
    }

    public void setWifiSummary(List<AppNetworkUsage> wifiSummary) {
        this.wifiSummary = wifiSummary;
    }

    public List<AppNetworkUsage> getMobileSummary() {
        return mobileSummary;
    }

    public void setMobileSummary(List<AppNetworkUsage> mobileSummary) {
        this.mobileSummary = mobileSummary;
    }
}
