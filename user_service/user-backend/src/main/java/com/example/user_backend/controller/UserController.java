package com.example.user_backend.controller;

import com.example.user_backend.model.User;
import com.example.user_backend.service.UserService;
import com.example.user_backend.repository.*;
import com.example.user_backend.mapper.*;
import com.example.user_backend.dto.*;
import jakarta.validation.Valid;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private Tracer tracer;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserRepository userRepository, UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;

    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRegisterRequestDTO dto) {
        Span span = tracer.nextSpan().name("manual-signup-span").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            logger.info("Inside manual signup span");
            User savedUser = userService.createUser(dto);
            return ResponseEntity.ok(userMapper.toResponse(savedUser));
        } finally {
            span.end();
        }
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> editProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid UserUpdateRequestDTO dto) {
        User updated = userService.updateUser(userId, dto);
        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(@RequestHeader("X-User-Id") String userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * @GetMapping("/search")
     * public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String
     * firstname) {
     * logger.info("Received search request for firstname: {}", firstname);
     * List<User> users =
     * userRepository.findByFirstnameContainingIgnoreCase(firstname);
     * List<UserResponseDTO> response = users.stream()
     * .map(userMapper::toResponse)
     * .toList();
     * return ResponseEntity.ok(response);
     * }
     */

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/type")
    public ResponseEntity<UserTypeResponseDTO> getUserType(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> new UserTypeResponseDTO(user.getId(), user.getType()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        logger.info("Looking up user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}/auth")
    public ResponseEntity<UserAuthDTO> getUserAuthByEmail(@PathVariable String email) {
        logger.info("Fetching user for auth by email: {}", email);

        return userRepository.findByEmail(email)
                .map(user -> {
                    UserAuthDTO dto = new UserAuthDTO();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setPassword(user.getPassword());
                    dto.setType(user.getType());
                    return dto;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sample")
    public ResponseEntity<List<UserEventDTO>> getSampleUsers(@RequestParam(defaultValue = "10") int limit) {
        List<UserEventDTO> users = userService.getRandomUsers(limit);
        return ResponseEntity.ok(users);
    }

}
