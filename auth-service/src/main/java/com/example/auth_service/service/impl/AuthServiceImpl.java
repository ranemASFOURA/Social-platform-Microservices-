package com.example.auth_service.service.impl;

import com.example.auth_service.client.UserClient;
import com.example.auth_service.dto.*;
import com.example.auth_service.service.IAuthService;
import com.example.auth_service.service.IJwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserClient userClient;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserClient userClient, IJwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userClient = userClient;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        UserAuthDTO user = userClient.getUserAuthByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

}
