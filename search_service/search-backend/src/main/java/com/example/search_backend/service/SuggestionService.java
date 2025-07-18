package com.example.search_backend.service;

import com.example.search_backend.model.UserDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

        private final RestTemplate restTemplate = new RestTemplate();

        @Value("${follow.service.following.ids.url}")
        private String followUrl;

        @Value("${user.service.sample.url}")
        private String usersUrl;

        public List<UserDocument> getSuggestions(String userId) {
                // 1. Get following IDs
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-User-Id", userId);
                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                ResponseEntity<List<String>> response = restTemplate.exchange(
                                followUrl,
                                HttpMethod.GET,
                                requestEntity,
                                new ParameterizedTypeReference<List<String>>() {
                                });
                List<String> followingIds = response.getBody();

                // 2. Get sample users
                ResponseEntity<List<UserDocument>> usersResponse = restTemplate.exchange(
                                usersUrl,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<List<UserDocument>>() {
                                });

                List<UserDocument> allUsers = usersResponse.getBody();

                // 3. Filter
                return allUsers.stream()
                                .filter(u -> !u.getId().equals(userId))
                                .filter(u -> !followingIds.contains(u.getId()))
                                .limit(4)
                                .collect(Collectors.toList());
        }
}
