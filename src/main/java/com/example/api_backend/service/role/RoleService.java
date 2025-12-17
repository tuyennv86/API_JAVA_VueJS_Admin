package com.example.api_backend.service.role;

import com.example.api_backend.dto.RoleDto;
import com.example.api_backend.mapper.MenuMapper;
import com.example.api_backend.mapper.PermissionMapper;
import com.example.api_backend.mapper.RoleMapper;
import com.example.api_backend.model.Menu;
import com.example.api_backend.model.Permission;
import com.example.api_backend.model.Role;
import com.example.api_backend.repository.MenuRepository;
import com.example.api_backend.repository.PermissionRepository;
import com.example.api_backend.repository.RoleRepository;
import com.example.api_backend.request.RoleRequest;
import com.example.api_backend.response.ListRoleResponse;
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

public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;
    private final MenuMapper menuMapper;
    private final PermissionMapper permissionMapper;


    @Override
    public List<RoleDto> getlist() {
        return roleMapper.toDtoList(roleRepository.findAll());
    }

    @Override
    public ListRoleResponse search(String keyword, Pageable pageable) {
        Page<Role> roles = roleRepository.findRoleSearch(keyword,pageable);
        return ListRoleResponse.builder().list(roleMapper.toDtoList(roles.stream().toList()))
                .total(roles.getTotalPages()).totalItems((int)roles.getTotalElements()).build();
    }

    @Override
    public RoleDto getById(Integer id) {
        Role role = roleRepository.findById(id).orElseThrow();
        Hibernate.initialize(role.getPermissions());
        Hibernate.initialize(role.getMenus());
        RoleDto roleDto = roleMapper.toDto(role);
        roleDto.setMenus(
                role.getMenus()
                        .stream()
                        .map(menuMapper::toDto)
                        .collect(Collectors.toList())
        );

        roleDto.setPermissions(
                role.getPermissions()
                        .stream()
                        .map(permissionMapper::toDto)
                        .collect(Collectors.toList())
        );
        return roleDto;
    }

    @Override
    public void deleteById(Integer id) {
        Role role = roleRepository.findById(id).orElseThrow(()->new RuntimeException("Role không tìm thấy"));
        role.getPermissions().clear(); // xóa permission
        role.getMenus().clear();// xoa menu
        roleRepository.save(role);
        // xóa role
        roleRepository.delete(role);
    }

    @Override
    public RoleDto save(RoleRequest roleRequest) {
        // kiem tra xem ten role da co hay chua
        if(roleRepository.existsByName(roleRequest.getName())) {
            throw new DataIntegrityViolationException(roleRequest.getName()+ ": Role này đã tồn tại");
        }
        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        if(roleRequest.getMenuIds() != null && !roleRequest.getMenuIds().isEmpty()) {
            Set<Menu> menus = new HashSet<>(menuRepository.findAllById(roleRequest.getMenuIds()));
            role.setMenus(menus);
        }
        if(roleRequest.getPermissionIds() != null && !roleRequest.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(roleRequest.getPermissionIds()));
            role.setPermissions(permissions);
        }
        roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto update(Integer id, RoleRequest roleRequest) {

        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin Role của id ="+ id));
        //Kiểm tra Role name nhưng bỏ qua Role name hiện tại
        Optional<Role> optionalRole = roleRepository.findByName(roleRequest.getName());
        if(optionalRole.isPresent() && !optionalRole.get().getId().equals(role.getId())) {
            throw new DataIntegrityViolationException("Role "+roleRequest.getName()+" đã tồn tại");
        }
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());

        // xóa bảng role_menu
        role.getMenus().clear();
        if(roleRequest.getMenuIds() != null && !roleRequest.getMenuIds().isEmpty()) {
            Set<Menu> menus = new HashSet<>(menuRepository.findAllById(roleRequest.getMenuIds()));
            //role.setMenus(menus);
            role.getMenus().addAll(menus);
        }
        if(roleRequest.getPermissionIds() != null && !roleRequest.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(roleRequest.getPermissionIds()));
//            role.setPermissions(permissions);
            role.getPermissions().addAll(permissions);
        }
        roleRepository.save(role);
        return roleMapper.toDto(role);
    }
}
