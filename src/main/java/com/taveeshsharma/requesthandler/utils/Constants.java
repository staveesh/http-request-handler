package com.taveeshsharma.requesthandler.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String BAD_REQUEST = "Bad request";
    public static final String UNAUTHORIZED = "Unauthorized";

    public static enum RequestType {SCHEDULE_MEASUREMENT, CHECKIN}

    ;

    public static enum MeasurementType {PING, DNS_LOOKUP, HTTP, TCP_SPEED_TEST, TRACEROUTE}

    ;
    public static final String TCP_TYPE = "tcp_speed_test",
            PING_TYPE = "ping",
            DNS_TYPE = "dns_lookup",
            HTTP_TYPE = "http",
            TRACERT_TYPE = "traceroute";
    public static final String ACCESS_POINT_TYPE = "ap_info";
    public static final String MOBILE_DEVICE_TYPE = "mobile_devices";

    // Contains assumed values of execution times (seconds) for each job type
    public static final Map<String, Long> JOB_EXECUTION_TIMES = new HashMap<String, Long>()
    {{
        put(PING_TYPE, 1L);
        put(DNS_TYPE, 6L);
        put(HTTP_TYPE, 6L);
        put(TRACERT_TYPE, 12L);
        put(TCP_TYPE, 16L);
    }};

    public static final long JOB_DISPATCHER_PERIOD_SECONDS = 30L;
    public static final long JOB_TRACKER_PERIOD_SECONDS = 20L;
    public static final long VPN_SERVER_DOWNLOADER_PERIOD_SECONDS = 3600L;
    public static final String VPN_SERVER_DOWNLOAD_URL = "http://www.vpngate.net/api/iphone/";
}
