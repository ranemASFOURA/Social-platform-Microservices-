package com.example.user_backend.kafka;

import com.example.user_backend.dto.*;
import com.example.user_backend.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private UserEventDTO toDTO(User user) {
        return new UserEventDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getImageUrl());
    }

    public void publishUserCreated(User user) {
        kafkaTemplate.send("user.created", toDTO(user));
    }

    public void publishUserUpdated(User user) {
        kafkaTemplate.send("user.updated", toDTO(user));
    }
}
