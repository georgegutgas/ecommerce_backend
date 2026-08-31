package com.ecommercefesti.ecommerce_backend.dto;

import com.ecommercefesti.ecommerce_backend.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String userEmail,
        BigDecimal totalAmount,
        String shippingAddress,
        String shippingCity,
        String shippingPostalCode,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {}