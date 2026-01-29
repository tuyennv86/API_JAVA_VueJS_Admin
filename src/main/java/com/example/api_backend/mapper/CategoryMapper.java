package com.example.api_backend.mapper;

import com.example.api_backend.dto.CategoryDto;
import com.example.api_backend.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);
    CategoryDto toDto(Category category);
    List<Category> toEntityList(List<CategoryDto> categoryDtoList);
    List<CategoryDto> toDtoList(List<Category> categoryList);
}
