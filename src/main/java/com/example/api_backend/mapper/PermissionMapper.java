package com.example.api_backend.mapper;

import com.example.api_backend.dto.PermissionDto;
import com.example.api_backend.model.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toEntity(PermissionDto permissionDto);
    PermissionDto toDto(Permission permission);

    List<PermissionDto> toDtoList(List<Permission> permissionList);
    List<Permission> toEntyList(List<PermissionDto> permissionDtoList);
}
