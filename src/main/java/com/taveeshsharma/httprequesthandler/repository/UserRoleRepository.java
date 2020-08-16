package com.taveeshsharma.httprequesthandler.repository;

import com.taveeshsharma.httprequesthandler.dto.documents.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRoleRepository extends MongoRepository<UserRole,String> {
    UserRole findByRole(String role);
}
