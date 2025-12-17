package com.example.api_backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {

    private Integer id;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Set<RoleDto> roles;
}
