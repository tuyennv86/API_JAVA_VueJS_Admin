package com.example.api_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name; // edit_users, view_users, ...

    private String description;

//    // JOIN role_permissions (owner là Role)
//    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)//chi load khi duoc goi ví dụ :  permission.getRoles().size();
//    @JsonIgnore
//    private Set<Role> roles = new HashSet<>();
//
//    // JOIN menu_permissions (owner là Menu)
//    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private Set<Menu> menus = new HashSet<>();

}