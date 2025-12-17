package com.example.api_backend.repository;

import com.example.api_backend.dto.MenuDto;
import com.example.api_backend.model.Menu;
import com.example.api_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> id(Integer id);

    @Query("SELECT DISTINCT m FROM Menu m JOIN m.roles r WHERE r.id IN :roleIds AND m.isActive = true ORDER BY m.sortOrder")
    List<Menu> findByRoleIds(@Param("roleIds") Set<Integer> roleIds);

    @Query("select m from Menu m where :keyword is null or :keyword = '' or lower(m.name) like lower(concat('%', :keyword, '%'))")
    List<Menu> findByKeyword(@Param("keyword") String keyword);
}
