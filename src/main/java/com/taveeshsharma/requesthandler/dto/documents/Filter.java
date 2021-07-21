package com.taveeshsharma.requesthandler.dto.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("filters")
public class Filter {
    private String vantagePoint;
    private String domain;
    private String recursive;
    private String dnsType;
    private Double sslTime;
    private Double pageLoadTime;

    @JsonProperty("vantagePoint")
    public String getVantagePoint() {
        return vantagePoint;
    }

    public void setVantagePoint(String vantagePoint) {
        this.vantagePoint = vantagePoint;
    }
    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("recursive")
    public String getRecursive() {
        return recursive;
    }

    public void setRecursive(String recursive) {
        this.recursive = recursive;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    @JsonProperty("dnsType")
    public String getDnsType() {
        return dnsType;
    }

    public void setDnsType(String dnsType) {
        this.dnsType = dnsType;
    }
    @JsonProperty("sslTime")
    public Double getSslTime() {
        return sslTime;
    }

    public void setSslTime(Double sslTime) {
        this.sslTime = sslTime;
    }
    @JsonProperty("pageLoadTime")
    public Double getPageLoadTime() {
        return pageLoadTime;
    }

    public void setPageLoadTime(Double pageLoadTime) {
        this.pageLoadTime = pageLoadTime;
    }
}
