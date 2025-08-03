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
import java.util.Map;
import java.util.Set;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class FeedService implements com.example.feed_backend.repository.FeedRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;


    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${follow.service.url}")
    private String followServiceUrl;

    @Autowired
    public FeedService(StringRedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }
    @Override
    public void distributePostToFollowers(FeedPost post) {
    String userId = post.getUserId();
    logger.info(" Distributing post for userId = {}", userId);

    String userType = post.getUserType();
    logger.info("Using userType from event = {}", userType);
    try {
        String postJson = objectMapper.writeValueAsString(post);
        logger.debug(" Post JSON: {}", postJson);

        if ("influencer".equalsIgnoreCase(userType)) {
            // 1. Store post in influencer feed
                String influencerFeedKey = "influencer:feed:" + userId;
                redisTemplate.opsForList().leftPush(influencerFeedKey, postJson);
                redisTemplate.opsForList().trim(influencerFeedKey, 0, 49);
                logger.info("Stored post in influencer feed: {}", influencerFeedKey);

                // 2. Save this user as influencer in all_influencers set
                redisTemplate.opsForSet().add("all_influencers", userId);


                // 3. Fetch followers and store them in influencer:followers:userId
                String url = followServiceUrl + "/followers/" + userId;
                logger.info("Fetching followers from: {}", url);

                ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
                Object[] followers = response.getBody();

                if (followers != null) {
                    String key = "influencer:followers:" + userId;
                    redisTemplate.delete(key); 

                    for (Object obj : followers) {
                        String followerId = extractFollowerId(obj);
                        if (followerId != null) {
                            redisTemplate.opsForList().rightPush(key, followerId);
                        }
                    }
                    logger.info("Stored {} followers for influencer {}", followers.length, userId);
                } else {
                    logger.warn("No followers returned from follow-service");
                }

            } else {
                // regular user
                String url = followServiceUrl + "/followers/" + userId;
                logger.info("Fetching followers from: {}", url);

                ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
                Object[] followers = response.getBody();

                if (followers != null) {
                    for (Object obj : followers) {
                        String followerId = extractFollowerId(obj);
                        if (followerId != null) {
                            String feedKey = "feed:" + followerId;
                            redisTemplate.opsForList().leftPush(feedKey, postJson);
                            redisTemplate.opsForList().trim(feedKey, 0, 49);
                            logger.debug("Distributed post to follower feed: {}", feedKey);
                        }
                    }
                    logger.info("Distributed post to {} followers", followers.length);
                } else {
                    logger.warn("No followers returned from follow-service");
                }
            }

        } catch (Exception e) {
            logger.error("Failed to distribute post: {}", e.getMessage(), e);
        }
    }

    private String extractFollowerId(Object obj) {
        try {
            String id = objectMapper.convertValue(obj, Map.class).get("followerId").toString();
            logger.debug("Extracted followerId: {}", id);
            return id;
        } catch (Exception e) {
            logger.error("Failed to extract followerId: {}", e.getMessage(), e);
            return null;
        }
    }

    public void removePostFromFeeds(String postId, String userId) {
    try {
        System.out.println(" Removing post " + postId + " from all feeds...");

        Set<String> feedKeys = redisTemplate.keys("feed:*");
        if (feedKeys != null) {
            for (String key : feedKeys) {
                List<String> posts = redisTemplate.opsForList().range(key, 0, -1);
                if (posts != null) {
                    for (String postJson : posts) {
                        if (postJson.contains(postId)) {
                            redisTemplate.opsForList().remove(key, 1, postJson);
                        }
                    }
                }
            }
        }

        String influencerKey = "influencer:feed:" + userId;
        List<String> influencerPosts = redisTemplate.opsForList().range(influencerKey, 0, -1);
        if (influencerPosts != null) {
            for (String postJson : influencerPosts) {
                if (postJson.contains(postId)) {
                    redisTemplate.opsForList().remove(influencerKey, 1, postJson);
                }
            }
        }

        System.out.println("Post removed from all feeds.");
    } catch (Exception e) {
        System.err.println(" Failed to remove post from feeds: " + e.getMessage());
    }
}
}
