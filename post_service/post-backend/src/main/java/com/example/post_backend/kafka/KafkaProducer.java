package com.example.post_backend.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPostCreatedEvent(String postId, String userId, String imageUrl, String caption) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "post.created");
        payload.put("postId", postId);
        payload.put("userId", userId);
        payload.put("imageUrl", imageUrl);
        payload.put("caption", caption);
        System.out.println("Sending Kafka post.created event...");

        kafkaTemplate.send("post.created", payload);
        System.out.println("Event sent successfully.");
    }
}
