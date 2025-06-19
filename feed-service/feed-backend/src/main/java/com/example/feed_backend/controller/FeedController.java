package com.example.feed_backend.controller;

import com.example.feed_backend.model.FeedPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;


import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private StringRedisTemplate redisTemplate;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
public List<FeedPost> getFeed(@RequestHeader("X-User-Id") String userId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {

        List<FeedPost> timeline = new ArrayList<>();
        int preloadLimit = 100;

        // 1. Fetch regular posts from feed:userId
        List<String> regularPostsJson = redisTemplate.opsForList().range("feed:" + userId, 0, preloadLimit - 1);
        if (regularPostsJson != null) {
            for (String json : regularPostsJson) {
                try {
                    timeline.add(objectMapper.readValue(json, FeedPost.class));
                } catch (Exception e) {
                    System.err.println("Failed to parse regular post: " + e.getMessage());
                }
            }
        }

        // 2. Fetch followed users
        String followUrl = "http://localhost:8083/api/follow/following/" + userId;
        ResponseEntity<Object[]> response = restTemplate.getForEntity(followUrl, Object[].class);
        Object[] following = response.getBody();

        if (following != null) {
            for (Object obj : following) {
                try {
                    Map<?, ?> map = objectMapper.convertValue(obj, Map.class);
                    String followedId = map.get("followingId").toString();

                    // 3. Fetch user type from user-service
                    String userTypeUrl = "http://localhost:8080/api/users/" + followedId + "/type";
                    ResponseEntity<Map> userTypeResp = restTemplate.getForEntity(userTypeUrl, Map.class);

                    if (userTypeResp.getStatusCode().is2xxSuccessful()) {
                        String type = userTypeResp.getBody().get("type").toString();
                        if ("influencer".equalsIgnoreCase(type)) {
    List<String> inflPosts = redisTemplate.opsForList().range("influencer:feed:" + followedId, 0, preloadLimit - 1);

                            if (inflPosts != null) {
                                for (String json : inflPosts) {
                                    try {
                                        timeline.add(objectMapper.readValue(json, FeedPost.class));
                                    } catch (Exception e) {
                                        System.err.println("Failed to parse influencer post: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error processing followed user: " + e.getMessage());
                }
            }
        }

        // 5. Sort timeline by timestamp (descending)
        //timeline.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        timeline.sort((a, b) -> {
    return Instant.parse(b.getTimestamp()).compareTo(Instant.parse(a.getTimestamp()));
});
    int start = page * size;
    int end = Math.min(start + size, timeline.size());

    if (start >= timeline.size()) {
        return List.of(); 
    }

    return timeline.subList(start, end);
}
}
