package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface JobRepository extends MongoRepository<Job, String> {

    @Query("{ 'startTime' : { $gte : ?0 } }")
    List<Job> getCurrentlyActiveJobs(Date currentTime);
}
