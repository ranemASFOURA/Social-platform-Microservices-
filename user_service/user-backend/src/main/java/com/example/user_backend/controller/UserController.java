package com.example.user_backend.controller;

import com.example.user_backend.model.User;
import com.example.user_backend.service.UserService;
import com.example.user_backend.repository.*;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.createUser(user);
        System.out.println("Creating user: " + user.getEmail());
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> editProfile(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String firstname) {
        logger.info("Received search request for firstname: {}", firstname);
        List<User> users = userRepository.findByFirstnameContainingIgnoreCase(firstname);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
