package com.birbuketmutluluk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderResponse(
        Long id,
        String orderNumber,
        String status,
        BigDecimal totalPrice,
        String customerNote,
        LocalDateTime createdAt,
        String customerName,
        String customerEmail,
        OrderAddressDto address,
        List<OrderItemResponse> items
) {
}
