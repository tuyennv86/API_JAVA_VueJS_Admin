package com.example.api_backend.service.typecode;

import com.example.api_backend.dto.TypeCodeDto;

import java.util.List;

public interface ITypeCodeService {
    List<TypeCodeDto> getAll();
    TypeCodeDto getById(int id);
}
