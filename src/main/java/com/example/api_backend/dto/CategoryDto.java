package com.example.api_backend.dto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CategoryDto {

    private Integer id;
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
    private LocalDateTime createdAt;
    private LocalDateTime modifyAt;
    private List<CategoryDto> children;
}
