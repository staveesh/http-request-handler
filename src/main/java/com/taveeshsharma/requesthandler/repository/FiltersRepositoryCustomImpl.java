package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.Cipher;
import com.taveeshsharma.requesthandler.dto.documents.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class FiltersRepositoryCustomImpl implements FiltersRepositoryCustom{

    @Autowired
    private MongoTemplate template;

    @Override
    public Filter findBestFilter(String networkType, String level) {
        Query query = new Query()
                .addCriteria(Criteria.where("networkType").is(networkType))
                .addCriteria(Criteria.where("level").is(level))
                .with(Sort.by(Sort.Direction.ASC, "pageLoadTime"))
                .limit(1);
        return template.findOne(query, Filter.class);
    }
}
