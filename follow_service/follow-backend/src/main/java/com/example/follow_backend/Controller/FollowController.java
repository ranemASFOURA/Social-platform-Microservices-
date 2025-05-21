package com.example.follow_backend.Controller;

import com.example.follow_backend.model.*;
import com.example.follow_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{followerId}/follow/{followingId}")
    public void follow(@PathVariable String followerId, @PathVariable String followingId) {
        followService.follow(followerId, followingId);
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public void unfollow(@PathVariable String followerId, @PathVariable String followingId) {
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

    @GetMapping("/{followerId}/is-following/{followingId}")
    public ResponseEntity<Void> isFollowing(@PathVariable String followerId, @PathVariable String followingId) {
        boolean exists = followService.isFollowing(followerId, followingId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

}
