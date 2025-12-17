package com.example.api_backend.service.role;

import com.example.api_backend.dto.RoleDto;
import com.example.api_backend.request.RoleRequest;
import com.example.api_backend.response.ListRoleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRoleService {
    List<RoleDto> getlist();
    ListRoleResponse search(String keyword, Pageable pageable);
    RoleDto getById(Integer id);
    void deleteById(Integer id);
    RoleDto save(RoleRequest roleRequest);
    RoleDto update(Integer id, RoleRequest roleRequest);
}