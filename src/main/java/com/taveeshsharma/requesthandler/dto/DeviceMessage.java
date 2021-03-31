package com.taveeshsharma.requesthandler.dto;

import java.util.Arrays;
import java.util.UUID;

public class DeviceMessage {
    private String id;
    private String time;
    private byte[] data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeviceMessage{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
