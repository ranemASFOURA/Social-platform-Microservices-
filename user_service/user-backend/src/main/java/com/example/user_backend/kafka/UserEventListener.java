package com.example.user_backend.kafka;

import com.example.user_backend.dto.InfluencerStatusChangedEvent;
import com.example.user_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @Autowired
    private MongoTemplate mongoTemplate;

    @KafkaListener(topics = "user.influencer.status.changed", groupId = "user-group")
    public void handleInfluencerStatusChanged(InfluencerStatusChangedEvent event) {
        System.out.println("Received: " + event.getUserId() + " => " + event.getType());

        Query query = new Query(Criteria.where("_id").is(event.getUserId()));
        Update update = new Update().set("type", event.getType());
        mongoTemplate.updateFirst(query, update, User.class);
    }

}
