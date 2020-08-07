package com.taveeshsharma.httprequesthandler.dto;

import java.math.BigDecimal;

public class TotalAppUsage {
    private String name;
    private BigDecimal Rx;
    private BigDecimal Tx;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRx() {
        return Rx;
    }

    public void setRx(BigDecimal rx) {
        Rx = rx;
    }

    public BigDecimal getTx() {
        return Tx;
    }

    public void setTx(BigDecimal tx) {
        Tx = tx;
    }

    @Override
    public String toString() {
        return "TotalAppUsage{" +
                "name='" + name + '\'' +
                ", Rx=" + Rx +
                ", Tx=" + Tx +
                '}';
    }
}
