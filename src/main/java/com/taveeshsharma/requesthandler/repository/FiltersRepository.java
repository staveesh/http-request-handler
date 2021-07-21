package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Filter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FiltersRepository extends MongoRepository<Filter, String> {

}
