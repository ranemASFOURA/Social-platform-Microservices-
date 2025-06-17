package com.example.auth_service.service;

import com.example.auth_service.dto.UserAuthDTO;

public interface IJwtService {
    String generateToken(UserAuthDTO user);
}
