package com.ecommercefesti.ecommerce_backend.dto;

import java.util.List;

public record OrderRequest(
        String shippingAddress,
        String shippingCity,
        String shippingPostalCode,
        List<OrderItemRequest> items
) {}