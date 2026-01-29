package com.example.api_backend.service.category;

import com.example.api_backend.dto.CategoryDto;
import com.example.api_backend.mapper.CategoryMapper;
import com.example.api_backend.model.Category;
import com.example.api_backend.model.Role;
import com.example.api_backend.repository.CategoryRepository;
import com.example.api_backend.request.CategoryRequest;
import com.example.api_backend.response.ListCategoryResponse;
import com.example.api_backend.service.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService fileStorageService;

    @Override
    public CategoryDto getCategoryById(int id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(buildCategoryTree(categories));
    }

    @Override
    public ListCategoryResponse searchByKeyword(String keyword, Pageable pageable) {
        Page<Category> categories = categoryRepository.findByKeyword(keyword,pageable);
        return ListCategoryResponse.builder().list(categoryMapper.toDtoList(buildCategoryTree(categories.getContent())))
                .total(categories.getTotalPages())
                .totalItems((int) categories.getTotalElements()).build();
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setPreview(categoryRequest.getPreview());
        category.setDetails(categoryRequest.getDetails());
        category.setCategoryTitle(categoryRequest.getCategoryTitle());
        category.setDescription(categoryRequest.getDescription());
        category.setKeywords(categoryRequest.getKeywords());
        category.setSortOrder(categoryRequest.getSortOrder());
        category.setParentId(categoryRequest.getParentId());
        // Ảnh upload — chỉ cần set đường dẫn tương đối (VD: "20251113/avatar.jpg")
        if (categoryRequest.getImageUrl() != null && !categoryRequest.getImageUrl().isBlank()) {
            category.setImageUrl(categoryRequest.getImageUrl());
        }
        category.setTypeCode(categoryRequest.getTypeCode());
        category.setIsActive(categoryRequest.getIsActive());
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(int id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(categoryRequest.getName());
        category.setPreview(categoryRequest.getPreview());
        category.setDetails(categoryRequest.getDetails());
        category.setCategoryTitle(categoryRequest.getCategoryTitle());
        category.setDescription(categoryRequest.getDescription());
        category.setKeywords(categoryRequest.getKeywords());
        category.setSortOrder(categoryRequest.getSortOrder());
        category.setParentId(categoryRequest.getParentId());
        // Ảnh upload — chỉ cần set đường dẫn tương đối (VD: "20251113/avatar.jpg")
        if (categoryRequest.getImageUrl() != null && !categoryRequest.getImageUrl().isBlank()) {
            // xóa ảnh cũ
            String oldFilePath = category.getImageUrl();
            if (oldFilePath != null && !oldFilePath.isBlank()) {
                fileStorageService.deleteFile(oldFilePath);
            }
            // cập nhật ảnh mới
            category.setImageUrl(categoryRequest.getImageUrl());
        }
        category.setTypeCode(categoryRequest.getTypeCode());
        category.setIsActive(categoryRequest.getIsActive());
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(int id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        if (category.getImageUrl() != null && !category.getImageUrl().isBlank()){
            fileStorageService.deleteFile(category.getImageUrl());
        }
        categoryRepository.delete(category);
    }

    // #region Build Tree
    //✅ Xây cây cha–con nếu có cha mà không tìm thấy cha thì đua lên đâu
    private List<Category> buildCategoryTree(List<Category> categories) {
        Map<Integer, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, m -> m));
        List<Category> rootCategories = new ArrayList<>();
        for (Category category : categories) {
            Integer parentId = category.getParentId();
            if (parentId == null) {
                rootCategories.add(category);
            } else {
                Category parent = categoryMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(category);
                } else {
                    // parent không tồn tại → đưa lên root
                    rootCategories.add(category);
                }
            }
        }
        sortRecursively(rootCategories);
        return rootCategories;
    }

    private void sortRecursively(List<Category> categories) {
        categories.sort(Comparator.comparing(Category::getSortOrder));

        for (Category category : categories) {
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                sortRecursively(category.getChildren());
            }
        }
    }
    // #endregion
}
