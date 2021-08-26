package com.taveeshsharma.requesthandler.network;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Link {
    private String node;
    private List<String> neighbors;

    @JsonProperty("node")
    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
    @JsonProperty("neighbors")
    public List<String> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<String> neighbors) {
        this.neighbors = neighbors;
    }
}
