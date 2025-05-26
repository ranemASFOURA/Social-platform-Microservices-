package com.example.user_backend.dto;

public class InfluencerStatusChangedEvent {
    private String userId;
    private String type; // "influencer" or "regular"

    public InfluencerStatusChangedEvent() {
    }

    public InfluencerStatusChangedEvent(String userId, String type) {
        this.userId = userId;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
