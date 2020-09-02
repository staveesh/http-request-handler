package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.ScheduleRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ScheduleRequestRepository extends MongoRepository<ScheduleRequest, String> {
    @Query("{'jobDescription.measurementDescription.type' : ?0}")
    List<ScheduleRequest> getScheduledJobsFromType(final String type);
}
