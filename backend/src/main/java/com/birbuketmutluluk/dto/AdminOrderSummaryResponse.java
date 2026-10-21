package com.birbuketmutluluk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminOrderSummaryResponse(
        Long id,
        String orderNumber,
        String customerName,
        String customerEmail,
        BigDecimal totalPrice,
        String status,
        LocalDateTime createdAt,
        int itemCount
) {
}
