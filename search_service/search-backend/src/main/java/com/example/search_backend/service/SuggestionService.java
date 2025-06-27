package com.example.search_backend.service;

import com.example.search_backend.model.UserDocument;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<UserDocument> getSuggestions(String userId) {
        // 1. Get the list of users the current user follows
        String followUrl = "http://localhost:8083/api/follow/following-ids";
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

        // 2. Get a sample of users from user-service
        String usersUrl = "http://localhost:8080/api/users/sample?limit=10";
        ResponseEntity<List<UserDocument>> usersResponse = restTemplate.exchange(
                usersUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDocument>>() {
                });

        List<UserDocument> allUsers = usersResponse.getBody();

        // 3. Filter users who are not followed yet
        return allUsers.stream()
                .filter(u -> !u.getId().equals(userId)) // استثني نفسك
                .filter(u -> !followingIds.contains(u.getId())) // فقط غير المتابعين
                .limit(4) // نعرض فقط 4 اقتراحات
                .collect(Collectors.toList());
    }
}
