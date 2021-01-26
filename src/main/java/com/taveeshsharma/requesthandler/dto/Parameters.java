package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class Parameters {
    private String target;
    private String server;
    private Boolean dirUp;
    private Boolean isExperiment;

    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @JsonProperty("server")
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @JsonProperty("dir_up")
    public Boolean getDirUp() {
        return dirUp;
    }

    public void setDirUp(Boolean dirUp) {
        this.dirUp = dirUp;
    }

    @JsonProperty("isExperiment")
    public Boolean getExperiment() {
        return isExperiment;
    }

    public void setExperiment(Boolean experiment) {
        isExperiment = experiment;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "target='" + target + '\'' +
                ", server='" + server + '\'' +
                ", dirUp=" + dirUp +
                ", isExperiment=" + isExperiment +
                '}';
    }
}
