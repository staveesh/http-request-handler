package com.taveeshsharma.httprequesthandler.repository;

import com.taveeshsharma.httprequesthandler.dto.documents.ScheduleRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ScheduleRequestRepository extends MongoRepository<ScheduleRequest, String> {
    @Query("{'jobDescription.measurementDescription.type' : ?0}")
    List<ScheduleRequest> getScheduledJobsFromType(final String type);
}
