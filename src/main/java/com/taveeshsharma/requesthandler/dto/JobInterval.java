package com.taveeshsharma.requesthandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobInterval {
    private Long hours;
    private Long minutes;
    private Long seconds;

    @JsonProperty("job_interval_hr")
    public Long getHours() {
        return hours;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }
    @JsonProperty("job_interval_min")
    public Long getMinutes() {
        return minutes;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }
    @JsonProperty("job_interval_sec")
    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return "JobInterval{" +
                "hours=" + hours +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                '}';
    }
}
