package com.example.api_backend.repository;

import com.example.api_backend.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByJti(String jti);
}