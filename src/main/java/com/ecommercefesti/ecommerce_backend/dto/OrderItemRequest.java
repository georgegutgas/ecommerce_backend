package com.ecommercefesti.ecommerce_backend.dto;

public record OrderItemRequest(
        Long productId,
        Integer quantity
) {}