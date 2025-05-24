package com.example.feed_backend.service;

import com.example.feed_backend.model.FeedPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService implements com.example.feed_backend.repository.FeedRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public FeedService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void distributePostToFollowers(FeedPost post) {
        String userId = post.getUserId();
        String url = "http://localhost:8083/api/follow/followers/" + userId;

        try {
            ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
            Object[] followers = response.getBody();

            if (followers != null) {
                String postJson = objectMapper.writeValueAsString(post);

                for (Object obj : followers) {
                    String followerId = extractFollowerId(obj);
                    if (followerId != null) {
                        String feedKey = "feed:" + followerId;
                        redisTemplate.opsForList().leftPush(feedKey, postJson);
                        redisTemplate.opsForList().trim(feedKey, 0, 49); 
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to distribute post: " + e.getMessage());
        }
    }

    
    private String extractFollowerId(Object obj) {
        try {
            return objectMapper.convertValue(obj, java.util.Map.class).get("followerId").toString();
        } catch (Exception e) {
            System.err.println("Failed to extract followerId: " + e.getMessage());
            return null;
        }
    }
}
