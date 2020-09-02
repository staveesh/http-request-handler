package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface PersonalDataRepository extends MongoRepository<PersonalData, String> {

    @Query("{ 'userName' : ?0, 'Date' : { $gte: ?1, $lte: ?2} }")
    List<PersonalData> getNetworkUsage(String userName,Date from, Date to);
}
