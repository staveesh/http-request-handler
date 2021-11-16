package com.taveeshsharma.requesthandler.dto;

import java.util.List;

public class NodesResponse<T> {
    private List<T> data;
    private List<String> deviceIds;

    public NodesResponse(List<T> data, List<String> deviceIds) {
        this.data = data;
        this.deviceIds = deviceIds;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
