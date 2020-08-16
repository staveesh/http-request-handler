package com.taveeshsharma.httprequesthandler.repository;

import com.taveeshsharma.httprequesthandler.dto.documents.User;
import com.taveeshsharma.httprequesthandler.dto.documents.UserRoles;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByUserNameAndRoles(String userName, Set<UserRoles> roles);
}
