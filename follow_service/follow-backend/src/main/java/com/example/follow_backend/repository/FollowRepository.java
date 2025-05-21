package com.example.follow_backend.repository;

import com.example.follow_backend.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByFollowerId(String followerId);

    List<Follow> findByFollowingId(String followingId);

    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);

    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}
