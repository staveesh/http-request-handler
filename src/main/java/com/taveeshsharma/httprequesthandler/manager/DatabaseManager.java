package com.taveeshsharma.httprequesthandler.manager;

import com.taveeshsharma.httprequesthandler.dto.ScheduleRequest;
import org.springframework.stereotype.Service;

@Service
public interface DatabaseManager {
    public void insertScheduledJob(ScheduleRequest request);
}
