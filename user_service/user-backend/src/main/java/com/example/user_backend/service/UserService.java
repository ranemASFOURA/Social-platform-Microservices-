package com.example.user_backend.service;

import com.example.user_backend.model.User;
import com.example.user_backend.repository.*;
//import org.modelmapper.ModelMapper;
import com.example.user_backend.Exception.EmailAlreadyExistsException;
import com.example.user_backend.Exception.UserNotFoundException;
import com.example.user_backend.dto.UserEventDTO;
import com.example.user_backend.dto.UserRegisterRequestDTO;
import com.example.user_backend.dto.UserUpdateRequestDTO;
import com.example.user_backend.kafka.KafkaEventPublisher;
import com.example.user_backend.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    // private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // @Autowired
    private MongoTemplate mongoTemplate;

    public UserService(UserRepository userRepository, KafkaEventPublisher eventPublisher,
            PasswordEncoder passwordEncoder, UserMapper userMapper, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
        // this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
    }

    public User createUser(UserRegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);
        logger.info("Saved user with ID: {}", saved.getId());
        eventPublisher.publishUserCreated(saved);
        return saved;

    }

    public User updateUser(String id, UserUpdateRequestDTO dto) {
        logger.info("Updating user with ID: {}", id);
        logger.info("Payload: firstname={}, lastname={}, email={}",
                dto.getFirstname(), dto.getLastname(), dto.getEmail());

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    throw new EmailAlreadyExistsException(dto.getEmail());
                }
            });
        }

        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();

        if (dto.getFirstname() != null)
            update.set("firstname", dto.getFirstname());
        if (dto.getLastname() != null)
            update.set("lastname", dto.getLastname());
        if (dto.getEmail() != null)
            update.set("email", dto.getEmail());
        if (dto.getImageUrl() != null)
            update.set("imageUrl", dto.getImageUrl());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            update.set("password", passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getBio() != null)
            update.set("bio", dto.getBio());

        mongoTemplate.updateFirst(query, update, User.class);
        User updatedUser = mongoTemplate.findById(id, User.class);

        eventPublisher.publishUserUpdated(updatedUser);

        return updatedUser;
    }

    // UserService.java
    public List<UserEventDTO> getRandomUsers(int limit) {
        return userRepository.findRandomUsers(limit);
    }

}
