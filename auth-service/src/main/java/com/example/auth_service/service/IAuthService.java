package com.example.auth_service.service;

import com.example.auth_service.dto.AuthRequest;
import com.example.auth_service.dto.AuthResponse;

public interface IAuthService {
    AuthResponse login(AuthRequest request);
}
