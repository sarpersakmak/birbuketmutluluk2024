package com.birbuketmutluluk.dto;

import com.birbuketmutluluk.entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        boolean active
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getDescription(),
                c.getImageUrl(),
                c.isActive()
        );
    }
}
