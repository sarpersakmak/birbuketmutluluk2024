package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Kategori adı zorunludur.")
        @Size(max = 120, message = "Kategori adı en fazla 120 karakter olabilir.")
        String name,

        @Size(max = 140, message = "Slug en fazla 140 karakter olabilir.")
        String slug,

        @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir.")
        String description,

        @Size(max = 500, message = "Görsel adresi en fazla 500 karakter olabilir.")
        String imageUrl,

        Boolean active
) {
}
