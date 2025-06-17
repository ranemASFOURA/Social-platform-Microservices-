package com.example.follow_backend.Controller;

import com.example.follow_backend.model.*;
import com.example.follow_backend.security.JwtUtil;
import com.example.follow_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;
    private final JwtUtil jwtUtil;

    @Autowired
    public FollowController(FollowService followService, JwtUtil jwtUtil) {
        this.followService = followService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/follow/{followingId}")
    public void follow(@RequestHeader("Authorization") String authHeader,
            @PathVariable String followingId) {
        String followerId = jwtUtil.extractUserId(authHeader);
        followService.follow(followerId, followingId);
    }

    @DeleteMapping("/unfollow/{followingId}")
    public void unfollow(@RequestHeader("Authorization") String authHeader,
            @PathVariable String followingId) {
        String followerId = jwtUtil.extractUserId(authHeader);
        followService.unfollow(followerId, followingId);
    }

    @GetMapping("/followers/{userId}")
    public List<Follow> getFollowers(@PathVariable String userId) {
        return followService.getFollowers(userId);
    }

    @GetMapping("/following/{userId}")
    public List<Follow> getFollowing(@PathVariable String userId) {
        return followService.getFollowing(userId);
    }

    @GetMapping("/is-following/{followingId}")
    public ResponseEntity<Void> isFollowing(@RequestHeader("Authorization") String authHeader,
            @PathVariable String followingId) {
        String followerId = jwtUtil.extractUserId(authHeader);
        boolean exists = followService.isFollowing(followerId, followingId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

}
