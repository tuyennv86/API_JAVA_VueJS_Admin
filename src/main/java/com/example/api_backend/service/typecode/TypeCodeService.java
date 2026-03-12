package com.example.api_backend.service.typecode;

import com.example.api_backend.dto.TypeCodeDto;
import com.example.api_backend.mapper.TypeCodeMapper;
import com.example.api_backend.model.TypeCodes;
import com.example.api_backend.repository.TypeCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class TypeCodeService implements ITypeCodeService{

    private final TypeCodeRepository typeCodeRepository;
    private final TypeCodeMapper typeCodeMapper;

    @Override
    public List<TypeCodeDto> getAll() {
        List<TypeCodes> list = typeCodeRepository.findAll();
        return typeCodeMapper.toDto(list);
    }

    @Override
    public TypeCodeDto getById(int id) {
        TypeCodes typeCodes = typeCodeRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tin thấy id :"+id) );
        return typeCodeMapper.toDto(typeCodes);
    }

}
