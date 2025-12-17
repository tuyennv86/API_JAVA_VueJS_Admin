package com.example.api_backend.service.permission;

import com.example.api_backend.dto.PermissionDto;
import com.example.api_backend.request.PermissionRequest;
import com.example.api_backend.response.ListPermissionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPermissionService {
    List<PermissionDto> getAll();
    ListPermissionResponse getSearch(String keyword, Pageable pageable);
    PermissionDto getById(Integer id);
    PermissionDto save(PermissionRequest permissionRequest);
    PermissionDto update(Integer id, PermissionRequest permissionRequest);
    void delete(Integer id);
}
