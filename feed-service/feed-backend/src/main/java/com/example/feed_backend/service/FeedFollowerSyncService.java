package com.example.feed_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Service
public class FeedFollowerSyncService {

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    @Value("${follow.service.url}")
    private String followServiceUrl;

    public FeedFollowerSyncService(StringRedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 300000)
    public void syncFollowersForInfluencers() {
        Set<String> influencers = redisTemplate.opsForSet().members("all_influencers");

        if (influencers == null || influencers.isEmpty()) return;

        for (String influencerId : influencers) {
            try {
                FollowDTO[] followers = restTemplate.getForObject(
                        followServiceUrl + "/followers/" + influencerId, FollowDTO[].class);

                if (followers == null) continue;

                String redisKey = "influencer:followers:" + influencerId;
                redisTemplate.delete(redisKey);

                for (FollowDTO f : followers) {
                    redisTemplate.opsForList().leftPush(redisKey, f.getFollowerId());
                }

                System.out.println("Synced followers for influencer " + influencerId);

            } catch (Exception e) {
                System.err.println("Failed to sync for influencer " + influencerId + ": " + e.getMessage());
            }
        }
    }

    public static class FollowDTO {
        private String followerId;

        public String getFollowerId() {
            return followerId;
        }

        public void setFollowerId(String followerId) {
            this.followerId = followerId;
        }
    }
}
