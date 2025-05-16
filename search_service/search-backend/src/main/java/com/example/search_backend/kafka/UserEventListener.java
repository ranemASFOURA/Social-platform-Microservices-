package com.example.search_backend.kafka;

import com.example.search_backend.model.UserDocument;
import com.example.search_backend.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final UserRepository userRepository;

    public UserEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = { "user.created",
            "user.updated" }, groupId = "search-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserEvent(UserDocument user) {
        userRepository.save(user);
        System.out.println("Received user event: " + user.getFirstname());
    }
}
