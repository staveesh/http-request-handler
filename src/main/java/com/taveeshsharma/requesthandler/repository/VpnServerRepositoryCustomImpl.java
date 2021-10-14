package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.VpnServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class VpnServerRepositoryCustomImpl implements VpnServerRepositoryCustom{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public VpnServer sortByPingAndSpeed() {
        Query query = new Query()
                .limit(1)
                .with(Sort.by(Sort.Direction.ASC, "ping"))
                .with(Sort.by(Sort.Direction.DESC, "speed"));
        VpnServer server = mongoTemplate.findOne(query, VpnServer.class);
        return server;
    }
}
