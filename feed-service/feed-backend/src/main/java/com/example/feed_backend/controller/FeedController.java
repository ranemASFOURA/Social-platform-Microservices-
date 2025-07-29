package com.example.feed_backend.controller;

import com.example.feed_backend.model.FeedPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;



import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;


@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
private RestTemplate restTemplate;

    @Value("${user.service.url}")
private String userServiceUrl;

@Value("${follow.service.url}")
private String followServiceUrl;



    private final ObjectMapper objectMapper = new ObjectMapper();
    

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

        // 2. Check influencer follow keys
        Set<String> keys = redisTemplate.keys("influencer:followers:*");
        if (keys != null) {
            for (String key : keys) {
                List<String> followers = redisTemplate.opsForList().range(key, 0, -1);
                if (followers != null && followers.contains(userId)) {
                    String influencerId = key.replace("influencer:followers:", "");
                    List<String> inflPosts = redisTemplate.opsForList().range("influencer:feed:" + influencerId, 0, preloadLimit - 1);
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
        }
        /// 3. Sort timeline by timestamp (descending)
        timeline.sort((a, b) -> Instant.parse(b.getTimestamp()).compareTo(Instant.parse(a.getTimestamp())));

        // 4. Pagination
        int start = page * size;
        int end = Math.min(start + size, timeline.size());

        if (start >= timeline.size()) {
            return List.of();
        }

        return timeline.subList(start, end);
    }
}

