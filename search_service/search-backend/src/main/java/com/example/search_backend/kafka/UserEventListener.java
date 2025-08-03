package com.example.search_backend.kafka;

import com.example.search_backend.dto.UserEventDTO;
import com.example.search_backend.model.UserDocument;
import com.example.search_backend.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final SearchService searchService;
    private final ObjectMapper objectMapper;

    public UserEventListener(SearchService searchService) {
        this.searchService = searchService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = { "user.created", "user.updated" }, groupId = "search-service-group")
    public void handleUserEvent(UserEventDTO userDto) {
        try {
            System.out.println("📥 Received event: " + userDto.getFirstname());

            UserDocument userDoc = new UserDocument(
                    userDto.getId(),
                    userDto.getFirstname(),
                    userDto.getLastname(),
                    userDto.getEmail(),
                    userDto.getImageUrl());

            searchService.saveUser(userDoc);

            System.out.println("Indexed user: " + userDoc.getFirstname());

        } catch (Exception e) {
            System.err.println("Failed to process user event: " + e.getMessage());
            e.printStackTrace();
        }
    }

}