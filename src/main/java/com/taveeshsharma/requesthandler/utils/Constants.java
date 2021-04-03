package com.taveeshsharma.requesthandler.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String BAD_REQUEST = "Bad request";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static enum RequestType {SCHEDULE_MEASUREMENT, CHECKIN};
    public static enum MeasurementType {PING, DNS_LOOKUP, HTTP, TCP_SPEED_TEST, TRACEROUTE};
    public static final String TCP_TYPE = "tcp_speed_test",
                               PING_TYPE = "ping",
                               DNS_TYPE = "dns_lookup",
                               HTTP_TYPE = "http",
                               TRACERT_TYPE = "traceroute";
    public static final String ACCESS_POINT_TYPE = "ap_info";
    public static final String MOBILE_DEVICE_TYPE = "mobile_devices";

    // Contains assumed values of execution times (minutes) for each job type
    public static final Map<String, Long> JOB_EXECUTION_TIMES = new HashMap<String, Long>() {{
        put(PING_TYPE, 37269L);
        put(DNS_TYPE, 441L);
        put(HTTP_TYPE, 1000L);
        put(TRACERT_TYPE, 20046L);
        put(TCP_TYPE, 102335L);
    }};

    public static final int MAX_TCP_MESSAGE_SIZE = 100000;
    // TODO: make it dynamic later
    public static final int MAX_NODES_ACTIVE = 5;
}
