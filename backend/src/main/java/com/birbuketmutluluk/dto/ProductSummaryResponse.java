package com.birbuketmutluluk.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Liste/kart için hafif ürün yanıtı. Kart galerisi için tüm görsel URL'leri
 * (ana görsel önce) imageUrls'te döner; mainImageUrl geriye dönük uyum içindir.
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
        String mainImageUrl,
        List<String> imageUrls
) {
}
