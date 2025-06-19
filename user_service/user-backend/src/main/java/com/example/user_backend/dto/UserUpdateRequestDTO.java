package com.example.user_backend.dto;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.Size;

public class UserUpdateRequestDTO {

    private String firstname;

    @Size(min = 2, max = 50)
    private String lastname;

    @Email

    private String email;

    @Size(min = 6, max = 100)
    private String password;

    private String imageUrl;

    // Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
