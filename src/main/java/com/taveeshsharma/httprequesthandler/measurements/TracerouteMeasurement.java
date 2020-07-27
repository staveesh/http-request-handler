package com.taveeshsharma.httprequesthandler.measurements;


import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

@Measurement(name="traceroute", timeUnit = TimeUnit.MILLISECONDS)
public class TracerouteMeasurement extends Measurements {
    @Column(name="num_hops")
    private Integer numberOfHops;

    @Column(name="hop_N_addr_i")
    private String listOfHopsIPAddress;

    @Column(name="hop_N_rtt_ms")
    private String listOfRTTs;

    public Integer getNumberOfHops() {
        return numberOfHops;
    }

    public void setNumberOfHops(Integer numberOfHops) {
        this.numberOfHops = numberOfHops;
    }

    public String getListOfHopsIPAddress() {
        return listOfHopsIPAddress;
    }

    public void setListOfHopsIPAddress(String listOfHopsIPAddress) {
        this.listOfHopsIPAddress = listOfHopsIPAddress;
    }

    public String getListOfRTTs() {
        return listOfRTTs;
    }

    public void setListOfRTTs(String listOfRTTs) {
        this.listOfRTTs = listOfRTTs;
    }
}
