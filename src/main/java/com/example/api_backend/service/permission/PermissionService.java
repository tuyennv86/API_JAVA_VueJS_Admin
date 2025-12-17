package com.example.api_backend.service.permission;

import com.example.api_backend.dto.PermissionDto;
import com.example.api_backend.mapper.MenuMapper;
import com.example.api_backend.mapper.PermissionMapper;
import com.example.api_backend.mapper.RoleMapper;
import com.example.api_backend.model.Menu;
import com.example.api_backend.model.Permission;
import com.example.api_backend.model.Role;
import com.example.api_backend.repository.MenuRepository;
import com.example.api_backend.repository.PermissionRepository;
import com.example.api_backend.repository.RoleRepository;
import com.example.api_backend.request.PermissionRequest;
import com.example.api_backend.response.ListPermissionResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;

    @Override
    public List<PermissionDto> getAll() {
        return permissionMapper.toDtoList(permissionRepository.findAll());
    }

    @Override
    public ListPermissionResponse getSearch(String keyword, Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findSeach(keyword, pageable);
        return ListPermissionResponse.builder()
                .list(permissionMapper.toDtoList(permissions.stream().toList()))
                .total(permissions.getTotalPages())
                .totalItems((int) permissions.getTotalElements()).build();
    }

    @Override
    public PermissionDto getById(Integer id) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new DataIntegrityViolationException("Permission not found"));
        return permissionMapper.toDto(permission);
    }

    @Override
    public PermissionDto save(PermissionRequest permissionRequest) {
        if(permissionRepository.existsByName(permissionRequest.getName())){
            throw new DataIntegrityViolationException("Tên này đã tồn tại");
        }
        Permission permission = new Permission();
        permission.setName(permissionRequest.getName());
        permission.setDescription(permissionRequest.getDescription());
        permissionRepository.save(permission);
        return permissionMapper.toDto(permission);
    }

    @Override
    public PermissionDto update(Integer id, PermissionRequest permissionRequest) {
        Permission permission = permissionRepository.findById(id).orElseThrow(()-> new RuntimeException("Không tìm thấy Permission có id ="+id));
        // kiểm tra xem tên Permission đã tồn tại hay chưa không tinh Permission hiện tại
        Optional<Permission> permissionOptional = permissionRepository.findByName(permissionRequest.getName());
        if(permissionOptional.isPresent() && !permissionOptional.get().getId().equals(permission.getId())){
            throw new DataIntegrityViolationException("Permission đã tồn tại :"+permissionRequest.getName());
        }

        permission.setName(permissionRequest.getName());
        permission.setDescription(permissionRequest.getDescription());
        permissionRepository.save(permission);
        return permissionMapper.toDto(permission);
    }

    @Override
    public void delete(Integer id) {
        // kiểm tra xem tồn tại hay không
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tồn tai Permission có id ="+id));
        permissionRepository.delete(permission);
    }
}
