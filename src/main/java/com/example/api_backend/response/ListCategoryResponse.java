package com.example.api_backend.response;

import com.example.api_backend.dto.CategoryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ListCategoryResponse {

    List<CategoryDto> list;
    int total;
    int totalItems; // tổng số bản ghi
}
