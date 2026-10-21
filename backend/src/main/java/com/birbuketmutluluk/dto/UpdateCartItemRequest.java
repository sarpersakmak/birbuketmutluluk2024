package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(

        @NotNull(message = "Adet zorunludur.")
        @Min(value = 1, message = "Adet en az 1 olmalıdır.")
        Integer quantity
) {
}
