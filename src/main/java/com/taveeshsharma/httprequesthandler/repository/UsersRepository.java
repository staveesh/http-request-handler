package com.taveeshsharma.httprequesthandler.repository;

import com.taveeshsharma.httprequesthandler.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<User, String> {

}
