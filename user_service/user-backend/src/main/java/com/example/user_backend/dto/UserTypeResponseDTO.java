package com.example.user_backend.dto;

public class UserTypeResponseDTO {
    private String userId;
    private String type;

    public UserTypeResponseDTO(String userId, String type) {
        this.userId = userId;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }
}
