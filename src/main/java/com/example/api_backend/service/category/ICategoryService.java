package com.example.api_backend.service.category;

import com.example.api_backend.dto.CategoryDto;
import com.example.api_backend.request.CategoryRequest;
import com.example.api_backend.response.ListCategoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    CategoryDto getCategoryById(int id);
    List<CategoryDto> getAllCategories();
    ListCategoryResponse searchByKeyword(String keyword, Pageable pageable);
    List<CategoryDto> getByTypeCode(String typeCode);
    List<CategoryDto> getOtherId(int id, String typeCode);

    CategoryDto createCategory(CategoryRequest categoryRequest);
    CategoryDto updateCategory(int id, CategoryRequest categoryRequest);
    CategoryDto isChangIsActive(int id);
    CategoryDto deleteImage(int id);
    void deleteCategory(int id);


}
