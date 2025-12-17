package com.example.api_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoginResponse {
    private UserInfo user;
    private String token;
    private String refreshToken;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String username;
        private String fullName;
        private String imageUrl;
        private List<String> roles;
        private List<String> permissions;
    }
}
