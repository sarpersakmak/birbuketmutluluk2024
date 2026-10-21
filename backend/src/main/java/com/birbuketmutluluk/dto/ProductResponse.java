package com.birbuketmutluluk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.birbuketmutluluk.entity.Product;

/**
 * Ürün detay yanıtı (tüm alanlar + görseller + kategori bilgisi).
 */
public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        boolean available,
        boolean featured,
        boolean customizable,
        String color,
        String size,
        String productionTime,
        String careInfo,
        Long categoryId,
        String categoryName,
        String categorySlug,
        List<ProductImageDto> images,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product p) {
        List<ProductImageDto> imgs = p.getImages().stream()
                .sorted(Comparator.comparing(i -> i.getDisplayOrder() == null ? 0 : i.getDisplayOrder()))
                .map(ProductImageDto::from)
                .toList();

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.isAvailable(),
                p.isFeatured(),
                p.isCustomizable(),
                p.getColor(),
                p.getSize(),
                p.getProductionTime(),
                p.getCareInfo(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getCategory().getSlug(),
                imgs,
                p.getCreatedAt()
        );
    }
}
