package com.example.api_backend.mapper;

import com.example.api_backend.dto.TypeCodeDto;
import com.example.api_backend.model.TypeCodes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MenuMapper.class, PermissionMapper.class})
public interface TypeCodeMapper {
    TypeCodes toEntity(TypeCodeDto typeCodeDto);
    TypeCodeDto toDto(TypeCodes typeCode);
    List<TypeCodeDto> toDto(List<TypeCodes> typeCodes);
    List<TypeCodes> toEntity(List<TypeCodeDto> typeCodeDtos);
}
