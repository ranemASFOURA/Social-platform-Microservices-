package com.example.feed_backend.service;

import com.example.feed_backend.model.FeedPost;
import com.example.feed_backend.dto.UserTypeResponse;
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
    String userType = getUserType(userId);

    try {
        String postJson = objectMapper.writeValueAsString(post);

        if ("influencer".equalsIgnoreCase(userType)) {
            // 1. Store influencer post in their own Redis feed
            String influencerFeedKey = "influencer:feed:" + userId;
            redisTemplate.opsForList().leftPush(influencerFeedKey, postJson);
            redisTemplate.opsForList().trim(influencerFeedKey, 0, 49);
            System.out.println("📦 Stored post for influencer: " + userId);
        } else {
            //  2. Distribute post to followers
            String url = "http://localhost:8083/api/follow/followers/" + userId;
            ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
            Object[] followers = response.getBody();

            if (followers != null) {
                for (Object obj : followers) {
                    String followerId = extractFollowerId(obj);
                    if (followerId != null) {
                        String feedKey = "feed:" + followerId;
                        redisTemplate.opsForList().leftPush(feedKey, postJson);
                        redisTemplate.opsForList().trim(feedKey, 0, 49);
                    }
                }
                System.out.println(" Distributed post to " + followers.length + " followers.");
            }
        }

    } catch (Exception e) {
        System.err.println("Failed to distribute post: " + e.getMessage());
    }
}


    private String getUserType(String userId) {
    try {
        String url = "http://localhost:8080/api/users/" + userId + "/type";
        ResponseEntity<UserTypeResponse> response = restTemplate.getForEntity(url, UserTypeResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getType();
        }
    } catch (Exception e) {
        System.err.println("Failed to get user type: " + e.getMessage());
    }
    return "regular"; // fallback
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
