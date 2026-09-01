package com.ecommercefesti.ecommerce_backend.dto;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description
) {}