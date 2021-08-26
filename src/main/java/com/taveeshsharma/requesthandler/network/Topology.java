package com.taveeshsharma.requesthandler.network;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Topology {
    private Integer nSwitches;
    private Integer nHosts;   // measurement nodes
    private Integer nTargets;
    private List<Link> links;
    // Each link has the same delay and bandwidth
    private String delay;
    private Integer bandwidth;

    @JsonProperty("nSwitches")
    public Integer getnSwitches() {
        return nSwitches;
    }

    public void setnSwitches(Integer nSwitches) {
        this.nSwitches = nSwitches;
    }
    @JsonProperty("nHosts")
    public Integer getnHosts() {
        return nHosts;
    }

    public void setnHosts(Integer nHosts) {
        this.nHosts = nHosts;
    }
    @JsonProperty("nTargets")
    public Integer getnTargets() {
        return nTargets;
    }

    public void setnTargets(Integer nTargets) {
        this.nTargets = nTargets;
    }
    @JsonProperty("links")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
    @JsonProperty("delay")
    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }
    @JsonProperty("bandwidth")
    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }
}
