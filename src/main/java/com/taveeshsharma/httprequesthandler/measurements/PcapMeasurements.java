package com.taveeshsharma.httprequesthandler.measurements;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * This is a class used to store pcap measurements. The pcap measurements consist of a destination and source address
 */
@Measurement(name = "pcap_data", timeUnit = TimeUnit.MILLISECONDS)
public class PcapMeasurements {

    @Column(name = "time")
    private Instant time;

    @Column(name = "destAddress")
    private String destinationAddress;

    @Column(name = "srcAddress")
    private String sourceAddress;

    public PcapMeasurements(long t, String dest, String src) {
        this.time = Instant.ofEpochMilli(t);
        this.destinationAddress = dest;
        this.sourceAddress = src;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
}
