package com.example.api_backend.request;

import lombok.Data;

@Data
public class AuthRequest {
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }


    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String fullName;
    }
}