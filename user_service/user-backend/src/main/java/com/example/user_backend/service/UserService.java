package com.example.user_backend.service;

import com.example.user_backend.model.User;
import com.example.user_backend.repository.*;
import com.example.user_backend.Exception.EmailAlreadyExistsException;
import com.example.user_backend.Exception.UserNotFoundException;
import com.example.user_backend.kafka.KafkaEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private MongoTemplate mongoTemplate;

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
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();

        if (user.getFirstname() != null) {
            update.set("firstname", user.getFirstname());
        }
        if (user.getLastname() != null) {
            update.set("lastname", user.getLastname());
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(userRepository.findById(id).map(User::getEmail).orElse(null))) {
                if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                    throw new EmailAlreadyExistsException(user.getEmail());
                }
            }
            update.set("email", user.getEmail());
        }
        if (user.getImageUrl() != null) {
            update.set("imageUrl", user.getImageUrl());
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            update.set("password", passwordEncoder.encode(user.getPassword()));
        }

        mongoTemplate.updateFirst(query, update, User.class);

        User updated = mongoTemplate.findById(id, User.class);
        eventPublisher.publishUserUpdated(updated);
        return updated;
    }

}
