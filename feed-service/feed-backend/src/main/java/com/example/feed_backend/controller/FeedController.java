package com.example.feed_backend.controller;

import com.example.feed_backend.model.FeedPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public List<FeedPost> getFeed(@RequestParam String userId) {
        List<String> rawPosts = redisTemplate.opsForList().range("feed:" + userId, 0, 49);
        List<FeedPost> feed = new ArrayList<>();

        if (rawPosts != null) {
            for (String json : rawPosts) {
                try {
                    FeedPost post = objectMapper.readValue(json, FeedPost.class);
                    feed.add(post);
                } catch (Exception e) {
                    System.err.println("Failed to parse post: " + e.getMessage());
                }
            }
        }

        return feed;
    }
}
