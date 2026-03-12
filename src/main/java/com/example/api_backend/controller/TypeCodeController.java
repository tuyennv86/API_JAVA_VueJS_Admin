package com.example.api_backend.controller;

import com.example.api_backend.dto.TypeCodeDto;
import com.example.api_backend.service.typecode.ITypeCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/typecodes")
@RequiredArgsConstructor

public class TypeCodeController {
    private final ITypeCodeService typeCodeService;

    @GetMapping("")
    public ResponseEntity<List<TypeCodeDto>> getAll(){
        return ResponseEntity.ok(typeCodeService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<TypeCodeDto> getById(@PathVariable int id){
        return new ResponseEntity<>(typeCodeService.getById(id), HttpStatus.OK);
    }
}
