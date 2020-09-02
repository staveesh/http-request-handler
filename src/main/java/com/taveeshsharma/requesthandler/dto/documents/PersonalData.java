package com.taveeshsharma.requesthandler.dto.documents;

import com.taveeshsharma.requesthandler.dto.AppNetworkUsage;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("personal")
public class PersonalData {
    private String requestType;
    private String userName;
    private Date Date;
    private List<AppNetworkUsage> userSummary;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public List<AppNetworkUsage> getUserSummary() {
        return userSummary;
    }

    public void setUserSummary(List<AppNetworkUsage> userSummary) {
        this.userSummary = userSummary;
    }

    @Override
    public String toString() {
        return "PersonalData{" +
                "requestType='" + requestType + '\'' +
                ", userName='" + userName + '\'' +
                ", Date=" + Date +
                ", userSummary=" + userSummary +
                '}';
    }
}
