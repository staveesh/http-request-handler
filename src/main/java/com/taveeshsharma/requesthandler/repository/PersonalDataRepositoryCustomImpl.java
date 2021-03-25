package com.taveeshsharma.requesthandler.repository;

import com.taveeshsharma.requesthandler.dto.documents.PersonalData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

public class PersonalDataRepositoryCustomImpl implements PersonalDataRepositoryCustom{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Date findLastSummaryCheckIn(String deviceId) {
        Query query = new Query()
                .addCriteria(Criteria.where("deviceId").is(deviceId))
                .limit(1)
                .with(Sort.by(Sort.Direction.DESC, "endTime"));
        PersonalData data = mongoTemplate.findOne(query, PersonalData.class);
        if(data != null)
            return data.getEndTime();
        return null;
    }
}
