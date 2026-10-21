package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(

        @NotNull(message = "Durum bilgisi zorunludur.")
        Boolean active
) {
}
