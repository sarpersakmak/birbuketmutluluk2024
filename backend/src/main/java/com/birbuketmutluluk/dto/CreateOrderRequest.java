package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(

        @NotNull(message = "Teslimat adresi zorunludur.")
        Long addressId,

        @Size(max = 1000, message = "Not en fazla 1000 karakter olabilir.")
        String customerNote
) {
}
