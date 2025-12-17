package com.example.api_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name; // admin, user, editor

    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Owner của role_permissions
    @ManyToMany(fetch = FetchType.LAZY) // FetchType.EAGER load luôn khi truy vấn
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // Owner của role_menus
    @ManyToMany(fetch = FetchType.LAZY) //FetchType.LAZY chỉ load khi duoc goi ví dụ :  role.getMenus().size();
    @JoinTable(name = "role_menus",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private Set<Menu> menus = new HashSet<>();
}