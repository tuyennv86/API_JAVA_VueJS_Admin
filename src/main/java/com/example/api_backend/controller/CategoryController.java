package com.example.api_backend.controller;

import com.example.api_backend.dto.CategoryDto;
import com.example.api_backend.dto.MenuDto;
import com.example.api_backend.model.Category;
import com.example.api_backend.request.CategoryRequest;
import com.example.api_backend.response.ListCategoryResponse;
import com.example.api_backend.response.MessageResponse;
import com.example.api_backend.service.FileStorageService;
import com.example.api_backend.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/category")

public class CategoryController {

    private final ICategoryService categoryService; 
    private final FileStorageService fileStorageService;

    @GetMapping("")
    public ResponseEntity<List<CategoryDto>> getCategory() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> GetById(@PathVariable Integer id){
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ListCategoryResponse> GetSearch(@RequestParam String keyword, @RequestParam Integer page, @RequestParam Integer size){
        PageRequest pageRequest = PageRequest.of(page - 1,size);
        ListCategoryResponse listCategoryResponse = categoryService.searchByKeyword(keyword, pageRequest);
        return new ResponseEntity<>(listCategoryResponse, HttpStatus.OK);
    }

    @GetMapping("getbytypecode/{typecode}")
    public ResponseEntity<List<CategoryDto>> GetByTypeCode(@PathVariable String typecode){
        return new ResponseEntity<>(categoryService.getByTypeCode(typecode), HttpStatus.OK);
    }

    @GetMapping("getotherId/{id}")
    public ResponseEntity<List<CategoryDto>> GetOtherId(@PathVariable Integer id){
        if(id != 0) {
            CategoryDto category = categoryService.getCategoryById(id);
            return new ResponseEntity<>(categoryService.getOtherId(id, category.getTypeCode()), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> UpdateCategory(@PathVariable Integer id,
                                                      @RequestPart("category") CategoryRequest categoryRequest,
                                                      @RequestPart(value = "image", required = false) MultipartFile imageFile){
        // Nếu có ảnh thì upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            categoryRequest.setImageUrl(fileName);
        }

        return new ResponseEntity<>(categoryService.updateCategory(id, categoryRequest), HttpStatus.OK);
    }

    @PostMapping("")
    public  ResponseEntity<CategoryDto> CreateCategory(@RequestPart("category") CategoryRequest categoryRequest,
                                                       @RequestPart(value = "image", required = false) MultipartFile imageFile){
        if (imageFile != null && !imageFile.isEmpty()) {
            // Trả về ví dụ: "20251113/avatar.png"
            String relativePath = fileStorageService.storeFile(imageFile);
            categoryRequest.setImageUrl(relativePath);
        }

        return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.OK);

    }
    @PutMapping("changIsActive/{id}")
    public ResponseEntity<CategoryDto> changIsActive(@PathVariable Integer id) {
        return new ResponseEntity<>(categoryService.isChangIsActive(id),HttpStatus.OK);
    }
    @PutMapping("deleteImage/{id}")
    public ResponseEntity<CategoryDto> deleteImage(@PathVariable Integer id) {
        return new ResponseEntity<>(categoryService.deleteImage(id),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> DeleteCategory(@PathVariable Integer id){
        try {
            categoryService.deleteCategory(id);
            return new ResponseEntity<>(new MessageResponse("User deleted successfully "+id), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
