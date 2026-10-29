package com.birbuketmutluluk.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "Ürün adı zorunludur.")
        @Size(max = 180, message = "Ürün adı en fazla 180 karakter olabilir.")
        String name,

        @Size(max = 200)
        String slug,

        String description,

        @NotNull(message = "Fiyat zorunludur.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat 0'dan büyük olmalıdır.")
        BigDecimal price,

        @NotNull(message = "Stok zorunludur.")
        @Min(value = 0, message = "Stok 0 veya daha büyük olmalıdır.")
        Integer stockQuantity,

        Long categoryId,

        Boolean available,
        Boolean featured,
        Boolean customizable,

        @Size(max = 60) String color,
        @Size(max = 60) String size,
        @Size(max = 100) String productionTime,
        @Size(max = 500) String careInfo,

        List<ProductImageDto> images
) {
}
