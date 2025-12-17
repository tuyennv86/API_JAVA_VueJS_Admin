package com.example.api_backend.repository;

import com.example.api_backend.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
    Boolean existsByName(String name);
    @Query("SELECT r FROM Role r WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Role> findRoleSearch(@Param("keyword") String keyword, Pageable pageable);
}