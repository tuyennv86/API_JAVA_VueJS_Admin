package com.example.api_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String jti;

    @Column(length = 2000)
    private String token;

    private LocalDateTime blacklistedAt = LocalDateTime.now();

    private LocalDateTime expiryDate;
}