package com.example.api_backend.response;

import com.example.api_backend.dto.UserDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ListUserResponse {
    List<UserDto> list;
    int total; // tổng số trang
    int totalItems; // tổng số bản ghi
}