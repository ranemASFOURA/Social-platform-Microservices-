package com.example.post_backend.controller;

import com.example.post_backend.model.Post;
import com.example.post_backend.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post.getUserId(), post.getCaption(), post.getImageUrl());
    }

    @GetMapping("/{userId}")
    public List<Post> getUserPosts(@PathVariable String userId) {
        return postService.getUserPosts(userId);
    }
}
