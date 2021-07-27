package com.taveeshsharma.requesthandler.dto.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("filters")
public class Filter {
    private String networkType;
    private String recursive;
    private String dnsType;
    private Double pageLoadTime;
    private String level;
    private String ipAddress;
    private String url;

    @JsonProperty("networkType")
    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    @JsonProperty("recursive")
    public String getRecursive() {
        return recursive;
    }

    public void setRecursive(String recursive) {
        this.recursive = recursive;
    }

    @JsonProperty("dnsType")
    public String getDnsType() {
        return dnsType;
    }

    public void setDnsType(String dnsType) {
        this.dnsType = dnsType;
    }

    @JsonProperty("pageLoadTime")
    public Double getPageLoadTime() {
        return pageLoadTime;
    }

    public void setPageLoadTime(Double pageLoadTime) {
        this.pageLoadTime = pageLoadTime;
    }

    @JsonProperty("level")
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    @JsonProperty("ipAddress")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
