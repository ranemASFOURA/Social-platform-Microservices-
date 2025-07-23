package com.example.follow_backend.service;

import com.example.follow_backend.dto.InfluencerStatusChangedEvent;
import com.example.follow_backend.model.*;
import com.example.follow_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private static final int INFLUENCER_THRESHOLD = 2;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.type.endpoint}")
    private String userTypeEndpoint;

    @Autowired
    public FollowService(FollowRepository followRepository, RestTemplate restTemplate) {
        this.followRepository = followRepository;
        this.restTemplate = restTemplate;
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void follow(String followerId, String followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            Follow follow = new Follow(followerId, followingId, Instant.now());
            followRepository.save(follow);
            sendInfluencerStatusUpdate(followingId);
        }
    }

    public void unfollow(String followerId, String followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        sendInfluencerStatusUpdate(followingId);
    }

    public List<Follow> getFollowers(String userId) {
        return followRepository.findByFollowingId(userId);
    }

    public List<Follow> getFollowing(String userId) {
        return followRepository.findByFollowerId(userId);
    }

    public boolean isFollowing(String followerId, String followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    private void sendInfluencerStatusUpdate(String userId) {
        long followerCount = followRepository.findByFollowingId(userId).size();
        String userType = getUserTypeFromUserService(userId);

        if (followerCount >= INFLUENCER_THRESHOLD && !"influencer".equals(userType)) {
            // Promote to influencer
            InfluencerStatusChangedEvent event = new InfluencerStatusChangedEvent(userId, "influencer");
            kafkaTemplate.send("user.influencer.status.changed", event);
        } else if (followerCount < INFLUENCER_THRESHOLD && !"regular".equals(userType)) {
            // Demote to regular
            InfluencerStatusChangedEvent event = new InfluencerStatusChangedEvent(userId, "regular");
            kafkaTemplate.send("user.influencer.status.changed", event);
        }
    }

    private String getUserTypeFromUserService(String userId) {
        try {
            String url = userTypeEndpoint.replace("{id}", userId);
            InfluencerStatusChangedEvent response = restTemplate.getForObject(url, InfluencerStatusChangedEvent.class);
            return response != null ? response.getType() : "regular";
        } catch (Exception e) {
            return "regular";
        }
    }

}
