package com.birbuketmutluluk.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSlug,
        String mainImageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
