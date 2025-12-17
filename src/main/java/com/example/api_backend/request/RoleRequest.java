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

public class RoleRequest {

    private String name;
    private String description;
    private Set<Integer> permissionIds;
    private Set<Integer> menuIds;
}
