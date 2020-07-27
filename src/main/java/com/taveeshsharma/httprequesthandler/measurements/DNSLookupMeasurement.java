package com.taveeshsharma.httprequesthandler.measurements;


import com.taveeshsharma.httprequesthandler.Constants;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.util.concurrent.TimeUnit;

/**
 * This is a dns measurement, which extends from the base measurement.
 * DNS measurement is used to store DNS results.
 */
@Measurement(name= Constants.DNS_TYPE, timeUnit = TimeUnit.MILLISECONDS)
public class DNSLookupMeasurement extends Measurements {

    @Column(name = "address")
    private String hostAddress;

    @Column(name="real_hostname")
    private String hostName;

    @Column(name ="time_ms")
    private Double timeTaken;

    //getters and setters of the above fields
    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Double getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Double timeTaken) {
        this.timeTaken = timeTaken;
    }
}
