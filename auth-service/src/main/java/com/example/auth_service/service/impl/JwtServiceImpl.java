package com.example.auth_service.service.impl;

import com.example.auth_service.dto.UserAuthDTO;
import com.example.auth_service.service.IJwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtServiceImpl implements IJwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public String generateToken(UserAuthDTO user) {
        return Jwts.builder()
                .setSubject(user.getId())
                .claim("email", user.getEmail())
                .claim("firstname", user.getFirstname())
                .claim("lastname", user.getLastname())
                .claim("type", user.getType())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 36000000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
