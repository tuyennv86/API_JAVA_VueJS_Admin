package com.example.api_backend.mapper;

import com.example.api_backend.dto.MenuDto;
import com.example.api_backend.model.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    Menu toEntity(MenuDto menuDto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
   // @Mapping(target = "children", ignore = true)
    MenuDto toDto(Menu menu);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    //@Mapping(target = "children", ignore = true)
    List<MenuDto> toDtoList(List<Menu> menus);

    List<Menu> toEntityList(List<MenuDto> menus);
}
