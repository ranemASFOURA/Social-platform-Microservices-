package com.example.search_backend.kafka;

import com.example.search_backend.model.UserDocument;
import com.example.search_backend.service.SearchService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final SearchService searchService;

    public UserEventListener(SearchService searchService) {
        this.searchService = searchService;
    }

    @KafkaListener(topics = { "user.created",
            "user.updated" }, groupId = "search-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserEvent(UserDocument user) {
        searchService.saveUser(user);
        System.out.println("Received user event: " + user.getFirstname());
    }
}
