package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.VpnServer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VpnServerRepository extends MongoRepository<VpnServer, String> {
}
