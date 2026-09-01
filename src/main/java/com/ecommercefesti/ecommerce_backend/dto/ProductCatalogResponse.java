package com.ecommercefesti.ecommerce_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductCatalogResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        CategoryResponse category,
        List<String> imageUrls
) {}