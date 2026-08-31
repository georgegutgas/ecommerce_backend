package com.ecommercefesti.ecommerce_backend.dto;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        String role
) {}