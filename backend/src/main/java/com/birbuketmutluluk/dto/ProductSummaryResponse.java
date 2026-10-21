package com.birbuketmutluluk.dto;

import java.math.BigDecimal;

/**
 * Liste/kart için hafif ürün yanıtı (ana görsel + temel alanlar).
 */
public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        Integer stockQuantity,
        boolean available,
        boolean featured,
        Long categoryId,
        String categoryName,
        String mainImageUrl
) {
}
