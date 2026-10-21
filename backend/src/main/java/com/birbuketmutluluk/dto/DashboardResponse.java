package com.birbuketmutluluk.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        long totalUsers,
        long activeUsers,
        long totalProducts,
        long totalOrders,
        long pendingOrders,
        long preparingOrders,
        long deliveredOrders,
        BigDecimal totalRevenue
) {
}
