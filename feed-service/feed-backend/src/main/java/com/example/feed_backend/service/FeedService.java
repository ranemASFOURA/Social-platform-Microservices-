package com.example.feed_backend.service;

import com.example.feed_backend.model.FeedPost;
import com.example.feed_backend.dto.UserTypeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class FeedService implements com.example.feed_backend.repository.FeedRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Value("${user.service.url}")
private String userServiceUrl;

@Value("${follow.service.url}")
private String followServiceUrl;

    @Autowired
    public FeedService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

@Override
public void distributePostToFollowers(FeedPost post) {
    String userId = post.getUserId();
    logger.info(" Distributing post for userId = {}", userId);

    String userType = getUserType(userId);
    logger.info(" Detected userType = {}", userType);

    try {
        String postJson = objectMapper.writeValueAsString(post);
        logger.debug(" Post JSON: {}", postJson);

        if ("influencer".equalsIgnoreCase(userType)) {
            String influencerFeedKey = "influencer:feed:" + userId;
            redisTemplate.opsForList().leftPush(influencerFeedKey, postJson);
            redisTemplate.opsForList().trim(influencerFeedKey, 0, 49);
            logger.info(" Stored post in influencer feed: {}", influencerFeedKey);
        } else {
            String url = followServiceUrl + "/followers/" + userId;
            logger.info(" Fetching followers from: {}", url);

            ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
            Object[] followers = response.getBody();

            if (followers != null) {
                for (Object obj : followers) {
                    String followerId = extractFollowerId(obj);
                    if (followerId != null) {
                        String feedKey = "feed:" + followerId;
                        redisTemplate.opsForList().leftPush(feedKey, postJson);
                        redisTemplate.opsForList().trim(feedKey, 0, 49);
                        logger.debug(" Distributed post to follower feed: {}", feedKey);
                    } else {
                        logger.warn(" followerId was null");
                    }
                }
                logger.info(" Distributed post to {} followers", followers.length);
            } else {
                logger.warn(" No followers returned from follow-service");
            }
        }

    } catch (Exception e) {
        logger.error(" Failed to distribute post: {}", e.getMessage(), e);
    }
}



    private String getUserType(String userId) {
    try {
        String url = userServiceUrl + "/" + userId + "/type";
        logger.info(" Calling user-service for type: {}", url);

        ResponseEntity<UserTypeResponse> response = restTemplate.getForEntity(url, UserTypeResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            logger.info(" User type fetched: {}", response.getBody().getType());
            return response.getBody().getType();
        } else {
            logger.warn(" Failed to get valid user type response");
        }
    } catch (Exception e) {
        logger.error(" Failed to get user type: {}", e.getMessage(), e);
    }
    return "regular"; // fallback
}


private String extractFollowerId(Object obj) {
    try {
        String id = objectMapper.convertValue(obj, java.util.Map.class).get("followerId").toString();
        logger.debug(" Extracted followerId: {}", id);
        return id;
    } catch (Exception e) {
        logger.error(" Failed to extract followerId: {}", e.getMessage(), e);
        return null;
    }
}


}
