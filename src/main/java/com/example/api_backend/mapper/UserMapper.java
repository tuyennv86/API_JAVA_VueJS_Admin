package com.example.api_backend.mapper;

import com.example.api_backend.dto.UserDto;
import com.example.api_backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    // Bỏ qua passwordHash khi map sang DTO
    @Mapping(target = "passwordHash", ignore = true)
    UserDto toDto(User user);

    // Khi nhận DTO từ client (nếu có tạo mới User)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserDto userDto);

    @Mapping(target = "passwordHash", ignore = true)
    List<User> toEntityList(List<UserDto> users);

    @Mapping(target = "passwordHash", ignore = true)
    List<UserDto> toDtoList(List<User> users);
}
