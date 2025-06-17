package com.example.user_backend.mapper;

import com.example.user_backend.controller.UserController;
import com.example.user_backend.dto.*;
import com.example.user_backend.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toEntity(UserRegisterRequestDTO dto) {
        logger.debug("Mapping DTO to Entity: {}", dto);
        return modelMapper.map(dto, User.class);
    }

    public User toEntity(UserUpdateRequestDTO dto) {
        logger.debug("Mapping DTO to Entity: {}", dto);
        return modelMapper.map(dto, User.class);
    }

    public UserResponseDTO toResponse(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public void updateEntity(User user, UserUpdateRequestDTO dto) {
        if (dto.getFirstname() != null)
            user.setFirstname(dto.getFirstname());
        if (dto.getLastname() != null)
            user.setLastname(dto.getLastname());
        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());
        if (dto.getImageUrl() != null)
            user.setImageUrl(dto.getImageUrl());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
        }
    }

}