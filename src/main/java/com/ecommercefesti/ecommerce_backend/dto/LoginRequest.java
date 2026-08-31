package com.ecommercefesti.ecommerce_backend.dto;

public record LoginRequest(
        String email,
        String password
) {}