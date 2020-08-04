package com.taveeshsharma.httprequesthandler.repository;

import com.taveeshsharma.httprequesthandler.dto.documents.PersonalData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonalDataRepository extends MongoRepository<PersonalData, String> {
}
