package com.example.follow_backend.service;

import com.example.follow_backend.dto.InfluencerStatusChangedEvent;
import com.example.follow_backend.model.*;
import com.example.follow_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private static final int INFLUENCER_THRESHOLD = 2;

    @Autowired
    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
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

        if (followerCount >= INFLUENCER_THRESHOLD) {
            InfluencerStatusChangedEvent event = new InfluencerStatusChangedEvent(userId, "influencer");
            kafkaTemplate.send("user.influencer.status.changed", event);
        }
    }

}
