package com.example.api_backend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserRequest {
    private String username;
    private String email;
    private String fullName;
    private String password;      // chỉ dùng khi thêm mới
    private String imageUrl;
    private Boolean isActive;
    private Set<Integer> roleIds;
}
