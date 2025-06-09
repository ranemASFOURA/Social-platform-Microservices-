package com.example.user_backend.controller;

import com.example.user_backend.model.User;
import com.example.user_backend.service.UserService;
import com.example.user_backend.repository.*;
import com.example.user_backend.mapper.*;
import com.example.user_backend.dto.*;
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
    private final UserMapper userMapper;

    public UserController(UserService userService, UserRepository userRepository, UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;

    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRegisterRequestDTO dto) {
        User savedUser = userService.createUser(dto);
        return ResponseEntity.ok(userMapper.toResponse(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> editProfile(@PathVariable String id,
            @RequestBody @Valid UserUpdateRequestDTO dto) {
        User updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String firstname) {
        logger.info("Received search request for firstname: {}", firstname);
        List<User> users = userRepository.findByFirstnameContainingIgnoreCase(firstname);
        List<UserResponseDTO> response = users.stream()
                .map(userMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

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

}
