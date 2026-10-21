package com.birbuketmutluluk.dto;

import java.util.List;

public record AdminUserDetailResponse(
        AdminUserResponse user,
        List<OrderSummaryResponse> orders
) {
}
