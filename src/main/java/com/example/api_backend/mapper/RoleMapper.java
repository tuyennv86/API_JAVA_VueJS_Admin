package com.example.api_backend.mapper;

import com.example.api_backend.dto.RoleDto;
import com.example.api_backend.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MenuMapper.class, PermissionMapper.class})
public interface RoleMapper {


    Role toEntity(RoleDto roleDto);

    @Mapping(target = "permissions", ignore = true) // Bỏ qua trường permissions khi MapStruct map DTO → Entity.
    @Mapping(target = "menus", ignore = true) // Bỏ qua trường permissions khi MapStruct map DTO → Entity.
    RoleDto toDto(Role role);

    List<Role> toEntityList(List<RoleDto> roleDtoList);

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "menus", ignore = true)
    List<RoleDto> toDtoList(List<Role> roleList);
}
