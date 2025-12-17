package com.example.api_backend.response;

import com.example.api_backend.dto.PermissionDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListPermissionResponse {
    List<PermissionDto> list;
    int total;
    int totalItems; // tổng số bản ghi
}
