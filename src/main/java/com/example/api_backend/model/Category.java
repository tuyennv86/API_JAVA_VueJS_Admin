package com.example.api_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String preview;
    private String details;
    private String categoryTitle;
    private String description;
    private String keywords;
    private Integer sortOrder;
    private Integer parentId;
    private String imageUrl;

    @Column(nullable = false)
    private String typeCode;
    private Boolean isActive;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifyAt = LocalDateTime.now();

    @Transient
    private List<Category> children = new ArrayList<>();

}
