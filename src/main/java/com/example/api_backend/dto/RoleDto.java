package com.example.api_backend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoleDto {
    private Integer id;
    private String name; // admin, user, editor
    private String description;
    private LocalDateTime createdAt;
    private List<PermissionDto> permissions;
    private List<MenuDto> menus;
}
