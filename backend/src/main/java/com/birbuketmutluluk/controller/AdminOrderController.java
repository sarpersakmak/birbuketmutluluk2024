package com.birbuketmutluluk.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.birbuketmutluluk.dto.AdminOrderResponse;
import com.birbuketmutluluk.dto.AdminOrderSummaryResponse;
import com.birbuketmutluluk.dto.UpdateOrderStatusRequest;
import com.birbuketmutluluk.entity.OrderStatus;
import com.birbuketmutluluk.service.AdminOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public List<AdminOrderSummaryResponse> list(@RequestParam(required = false) OrderStatus status) {
        return adminOrderService.listOrders(status);
    }

    @GetMapping("/{id}")
    public AdminOrderResponse detail(@PathVariable Long id) {
        return adminOrderService.getOrder(id);
    }

    @PutMapping("/{id}/status")
    public AdminOrderResponse updateStatus(@PathVariable Long id,
                                           @Valid @RequestBody UpdateOrderStatusRequest request) {
        return adminOrderService.updateStatus(id, request.status());
    }
}
