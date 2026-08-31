package com.ecommercefesti.ecommerce_backend.dto;

import com.ecommercefesti.ecommerce_backend.entity.OrderStatus;

public record OrderStatusUpdateRequest(
        OrderStatus status
) {}