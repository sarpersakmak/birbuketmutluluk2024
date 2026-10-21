package com.birbuketmutluluk.dto;

import com.birbuketmutluluk.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

        @NotNull(message = "Durum zorunludur.")
        OrderStatus status
) {
}
