package com.taveeshsharma.httprequesthandler.repository;

import com.taveeshsharma.httprequesthandler.dto.MeasurementDescription;
import com.taveeshsharma.httprequesthandler.dto.ScheduleRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRequestRepository extends MongoRepository<ScheduleRequest, String> {

}
