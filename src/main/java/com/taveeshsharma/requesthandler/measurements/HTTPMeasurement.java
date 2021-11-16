package com.taveeshsharma.requesthandler.measurements;


import com.taveeshsharma.requesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

/**
 * Http measurement, which extends from the base measurement.
 */
@Measurement(name = Constants.HTTP_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class HTTPMeasurement extends Measurements {
    @Column(name = "dns_time")
    private Double dnsTime;
    @Column(name = "ssl_time")
    private Double sslTime;
    @Column(name = "tcp_time")
    private Double tcpTime;
    @Column(name = "page_load_time")
    private Double pageLoadTime;
    @Column(name = "rtt_page")
    private Double rttPage;
    @Column(name = "rtt_resolver")
    private Double rttResolver;

    public Double getDnsTime() {
        return dnsTime;
    }

    public void setDnsTime(Double dnsTime) {
        this.dnsTime = dnsTime;
    }

    public Double getSslTime() {
        return sslTime;
    }

    public void setSslTime(Double sslTime) {
        this.sslTime = sslTime;
    }

    public Double getTcpTime() {
        return tcpTime;
    }

    public void setTcpTime(Double tcpTime) {
        this.tcpTime = tcpTime;
    }

    public Double getPageLoadTime() {
        return pageLoadTime;
    }

    public void setPageLoadTime(Double pageLoadTime) {
        this.pageLoadTime = pageLoadTime;
    }

    public Double getRttPage() {
        return rttPage;
    }

    public void setRttPage(Double rttPage) {
        this.rttPage = rttPage;
    }

    public Double getRttResolver() {
        return rttResolver;
    }

    public void setRttResolver(Double rttResolver) {
        this.rttResolver = rttResolver;
    }
}
