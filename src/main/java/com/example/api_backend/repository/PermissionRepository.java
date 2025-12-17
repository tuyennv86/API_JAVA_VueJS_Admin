package com.example.api_backend.repository;


import com.example.api_backend.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Boolean existsByName(String name);
    Optional<Permission> findByName(String name);
    Page<Permission> findByNameContaining(String name, Pageable pageable);

    @Query("select p from Permission p where (:keyword is null or :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Permission> findSeach(@Param("keyword") String keyword, Pageable pageable);
}
