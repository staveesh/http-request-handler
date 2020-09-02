package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameters {
    private String target;
    private String server;
    private Boolean dirUp;

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

    @Override
    public String toString() {
        return "Parameters{" +
                "target='" + target + '\'' +
                ", server='" + server + '\'' +
                ", dirUp=" + dirUp +
                '}';
    }
}
