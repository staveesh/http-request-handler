package com.taveeshsharma.requesthandler.dto.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("vpn_servers")
public class VpnServer {
    @Id
    private String hostName;
    private String ip;
    private Long score;
    private Integer ping;
    private Long speed;
    private String country;
    private String countryCode;
    private Integer numVpnSessions;
    private Long uptime;
    private Long totalUsers;
    private Long totalTraffic;
    private String logType;
    private String operator;
    private String message;
    private String configData;

    @JsonProperty("hostName")
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    @JsonProperty("score")
    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
    @JsonProperty("ping")
    public Integer getPing() {
        return ping;
    }

    public void setPing(Integer ping) {
        this.ping = ping;
    }
    @JsonProperty("speed")
    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    @JsonProperty("numVpnSessions")
    public Integer getNumVpnSessions() {
        return numVpnSessions;
    }

    @JsonProperty("countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setNumVpnSessions(Integer numVpnSessions) {
        this.numVpnSessions = numVpnSessions;
    }
    @JsonProperty("uptime")
    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }
    @JsonProperty("totalUsers")
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
    @JsonProperty("totalTraffic")
    public Long getTotalTraffic() {
        return totalTraffic;
    }

    public void setTotalTraffic(Long totalTraffic) {
        this.totalTraffic = totalTraffic;
    }
    @JsonProperty("logType")
    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @JsonProperty("configData")
    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }
}
