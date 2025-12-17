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
public class PermissionRequest {
    private String name; // edit_users, view_users, ...
    private String description;
//    private Set<Integer> menuIds;
//    private Set<Integer> roleIds;
}
