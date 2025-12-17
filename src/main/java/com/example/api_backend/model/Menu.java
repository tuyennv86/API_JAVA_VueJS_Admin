package com.example.api_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String path;

    private String icon;

    private Integer parentId;

    private Integer sortOrder = 0;

    private Boolean isActive = true;

    // Role - Menu => OWNER là Role, nên Menu là inverse
    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    // Menu - Permission => OWNER là Menu
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_permissions",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @Transient //không lưu trường này vào DB chỉ dùng làm danh sách con de Build menu
    private List<Menu> children = new ArrayList<>();
}
