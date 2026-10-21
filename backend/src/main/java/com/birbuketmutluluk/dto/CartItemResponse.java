package com.birbuketmutluluk.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSlug,
        String mainImageUrl,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        boolean available,
        Integer stockQuantity
) {
}
