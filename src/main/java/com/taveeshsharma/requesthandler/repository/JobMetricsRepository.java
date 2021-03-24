package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.JobMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface JobMetricsRepository extends MongoRepository<JobMetrics, String> {

    @Query("{ 'jobKey' : ?0, 'instanceNumber' : ?1 }")
    JobMetrics findByKeyAndInstanceNumber(String key, int instanceNumber);
}
