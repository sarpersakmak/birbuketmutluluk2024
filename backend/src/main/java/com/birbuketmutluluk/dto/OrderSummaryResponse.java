package com.birbuketmutluluk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        String status,
        BigDecimal totalPrice,
        int itemCount,
        LocalDateTime createdAt
) {
}
