package com.example.api_backend.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TypeCodeDto {
    private Integer id;
    private String typeCode;
    private String details;
}
