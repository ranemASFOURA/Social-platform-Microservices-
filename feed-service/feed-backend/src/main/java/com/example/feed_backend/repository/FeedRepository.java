package com.example.feed_backend.repository;

import com.example.feed_backend.model.FeedPost;


public interface FeedRepository {
    void distributePostToFollowers(FeedPost post);
}
