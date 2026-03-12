package com.example.api_backend.repository;

import com.example.api_backend.model.TypeCodes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeCodeRepository extends JpaRepository<TypeCodes, Integer> {
}
