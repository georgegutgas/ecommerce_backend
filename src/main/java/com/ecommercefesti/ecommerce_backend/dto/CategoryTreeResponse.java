package com.ecommercefesti.ecommerce_backend.dto;

// CategoryTreeResponseDto.java
import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String name,
        String slug,
        List<SubcategoryResponse> subcategories
) {}
