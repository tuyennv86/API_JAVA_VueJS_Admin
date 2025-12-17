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

public class MenuRequest {

    private String name;
    private String path;
    private String icon;
    private Integer parentId;
    private Integer sortOrder;
    private Boolean isActive;
    private Set<Integer> permissionIds;
}
