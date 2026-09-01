package com.ecommercefesti.ecommerce_backend.dto;

import jakarta.validation.constraints.NotNull;

public record StockUpdateRequest(
        @NotNull(message = "La cantidad es obligatoria")
        Integer stock
) {}