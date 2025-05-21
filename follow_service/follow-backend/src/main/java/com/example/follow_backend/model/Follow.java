package com.example.follow_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("follows")
public class Follow {

    @Id
    private String id;

    private String followerId;
    private String followingId;
    private Instant followedAt;

    public Follow() {
    }

    public Follow(String followerId, String followingId, Instant followedAt) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.followedAt = followedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public Instant getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(Instant followedAt) {
        this.followedAt = followedAt;
    }
}