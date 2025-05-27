package com.example.feed_backend.dto;

public class UserTypeResponse {
    private String userId;
    private String type;

    public UserTypeResponse() {}

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
