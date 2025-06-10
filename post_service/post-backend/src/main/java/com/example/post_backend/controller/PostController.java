package com.example.post_backend.controller;

import com.example.post_backend.model.Post;
import com.example.post_backend.service.PostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

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
    public Page<Post> getUserPosts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postService.getUserPosts(userId, pageable);
    }
}
