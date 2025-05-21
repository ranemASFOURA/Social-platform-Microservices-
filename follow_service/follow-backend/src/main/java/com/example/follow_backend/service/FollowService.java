package com.example.follow_backend.service;

import com.example.follow_backend.model.*;
import com.example.follow_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class FollowService {

    private final FollowRepository followRepository;

    @Autowired
    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void follow(String followerId, String followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            Follow follow = new Follow(followerId, followingId, Instant.now());
            followRepository.save(follow);
        }
    }

    public void unfollow(String followerId, String followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<Follow> getFollowers(String userId) {
        return followRepository.findByFollowingId(userId);
    }

    public List<Follow> getFollowing(String userId) {
        return followRepository.findByFollowerId(userId);
    }
}
