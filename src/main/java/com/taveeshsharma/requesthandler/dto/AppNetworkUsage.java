package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppNetworkUsage {
    private String name;
    private Long rx;
    private Long tx;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Rx")
    public Long getRx() {
        return rx;
    }

    public void setRx(Long rx) {
        this.rx = rx;
    }

    @JsonProperty("Tx")
    public Long getTx() {
        return tx;
    }

    public void setTx(Long tx) {
        this.tx = tx;
    }

    @Override
    public String toString() {
        return "AppNetworkUsage{" +
                "name='" + name + '\'' +
                ", rx=" + rx +
                ", tx=" + tx +
                '}';
    }
}
