package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppNetworkUsage {
    private String name;
    private Long Rx;
    private Long Tx;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Rx")
    public Long getRx() {
        return Rx;
    }

    public void setRx(Long rx) {
        Rx = rx;
    }

    @JsonProperty("Tx")
    public Long getTx() {
        return Tx;
    }

    public void setTx(Long tx) {
        Tx = tx;
    }

    @Override
    public String toString() {
        return "AppNetworkUsage{" +
                "name='" + name + '\'' +
                ", Rx=" + Rx +
                ", Tx=" + Tx +
                '}';
    }
}
