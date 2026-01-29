package com.example.api_backend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CategoryRequest {
    private String name;
    private String preview;
    private String details;
    private String categoryTitle;
    private String description;
    private String keywords;
    private Integer sortOrder;
    private Integer parentId;
    private String imageUrl;
    private String typeCode;
    private Boolean isActive;
}
