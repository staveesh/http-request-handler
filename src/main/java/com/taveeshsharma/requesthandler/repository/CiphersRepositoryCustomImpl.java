package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Cipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class CiphersRepositoryCustomImpl implements CiphersRepositoryCustom{

    @Autowired
    private MongoTemplate template;

    @Override
    public Cipher findBestCipherByLevel(String level) {
        Query query = new Query()
                .addCriteria(Criteria.where("level").is(level))
                .with(Sort.by(Sort.Direction.ASC, "rank"))
                .limit(1);
        return template.findOne(query, Cipher.class);
    }
}
