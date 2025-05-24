package com.example.feed_backend.model;

public class FeedPost {
    private String postId;
    private String userId;
    private String imageUrl;
    private String caption;
    private String timestamp;

    public FeedPost() {
    }

    public FeedPost(String postId, String userId, String imageUrl, String caption, String timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.timestamp = timestamp;
    }

    // Getters and Setters 
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
