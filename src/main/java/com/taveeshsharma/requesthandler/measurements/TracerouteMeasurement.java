package com.taveeshsharma.requesthandler.measurements;


import com.taveeshsharma.requesthandler.utils.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

@Measurement(name= Constants.TRACERT_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class TracerouteMeasurement extends Measurements {

    @Column(name="num_hops")
    private Integer numberOfHops;

    @Column(name="hop_N_addr_1")
    private String listOfHopsIPAddress;

    @Column(name="hop_N_rtt_ms")
    private String listOfRTTs;

    @Column(name = "time_ms")
    private Long timeMs;

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

    public Long getTimeMs() {
        return timeMs;
    }

    public void setTimeMs(Long timeMs) {
        this.timeMs = timeMs;
    }
}
