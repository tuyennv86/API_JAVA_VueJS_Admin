package com.example.api_backend.dto;

import com.example.api_backend.model.Permission;
import com.example.api_backend.model.Role;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MenuDto {
    private Integer id;
    private String name;
    private String path;
    private String icon;
    private Integer parentId;
    private Integer sortOrder;
    private Boolean isActive;
    private Set<Role> roles;
    private Set<Permission> permissions;
    private List<MenuDto> children;
}
