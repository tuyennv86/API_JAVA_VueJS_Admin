package com.example.api_backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PermissionDto {
    private Integer id;
    private String name;
    private String description;
//    private List<RoleDto> roles;
//    private List<MenuDto> menus;
}
