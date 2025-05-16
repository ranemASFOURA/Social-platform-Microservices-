package com.example.user_backend.service;

import com.example.user_backend.model.User;
import com.example.user_backend.repository.*;
import com.example.user_backend.Exception.EmailAlreadyExistsException;
import com.example.user_backend.Exception.UserNotFoundException;
import com.example.user_backend.kafka.KafkaEventPublisher;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, KafkaEventPublisher eventPublisher,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        eventPublisher.publishUserCreated(saved);
        System.out.println("Saving user with imageUrl = " + user.getImageUrl());
        return saved;
    }

    public User updateUser(String id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException(user.getEmail());
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getFirstname() != null) {
            existingUser.setFirstname(user.getFirstname());
        }

        if (user.getLastname() != null) {
            existingUser.setLastname(user.getLastname());
        }

        if (user.getImageUrl() != null) {
            existingUser.setImageUrl(user.getImageUrl());
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updated = userRepository.save(existingUser);
        eventPublisher.publishUserUpdated(updated);
        return updated;
    }

}
