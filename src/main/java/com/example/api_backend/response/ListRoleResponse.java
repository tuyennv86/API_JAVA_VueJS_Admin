package com.example.api_backend.response;

import com.example.api_backend.dto.RoleDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ListRoleResponse {
    List<RoleDto> list;
    int total;
    int totalItems; // tổng số bản ghi
}
