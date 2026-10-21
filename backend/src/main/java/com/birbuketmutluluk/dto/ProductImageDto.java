package com.birbuketmutluluk.dto;

import com.birbuketmutluluk.entity.ProductImage;

/**
 * Hem istek (oluşturma/güncelleme) hem yanıt için kullanılır.
 * İstekte id null olabilir.
 */
public record ProductImageDto(
        Long id,
        String imageUrl,
        Integer displayOrder,
        boolean isMain
) {
    public static ProductImageDto from(ProductImage img) {
        return new ProductImageDto(img.getId(), img.getImageUrl(), img.getDisplayOrder(), img.isMain());
    }
}
