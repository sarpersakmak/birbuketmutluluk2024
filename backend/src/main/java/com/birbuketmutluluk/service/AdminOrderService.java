package com.birbuketmutluluk.service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.AdminOrderResponse;
import com.birbuketmutluluk.dto.AdminOrderSummaryResponse;
import com.birbuketmutluluk.dto.OrderAddressDto;
import com.birbuketmutluluk.dto.OrderItemResponse;
import com.birbuketmutluluk.entity.Order;
import com.birbuketmutluluk.entity.OrderItem;
import com.birbuketmutluluk.entity.OrderStatus;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.OrderRepository;

@Service
public class AdminOrderService {

    /** İzinli durum geçişleri (bölüm 17). */
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.PREPARING, EnumSet.of(OrderStatus.READY, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.READY, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminOrderSummaryResponse> listOrders(OrderStatus status) {
        return orderRepository.findAllForAdmin(status).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminOrderResponse getOrder(Long id) {
        Order order = orderRepository.findAdminDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı."));
        return toDetail(order);
    }

    @Transactional
    public AdminOrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findAdminDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı."));

        OrderStatus current = order.getStatus();
        if (current == newStatus) {
            throw new ApiException(HttpStatus.CONFLICT, "Sipariş zaten bu durumda.");
        }
        if (!ALLOWED.getOrDefault(current, EnumSet.noneOf(OrderStatus.class)).contains(newStatus)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Bu sipariş '" + current + "' durumundan '" + newStatus + "' durumuna geçirilemez.");
        }

        // İptalde stok iadesi (mevcut ürünlere, tek transaction).
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null && product.getStockQuantity() != null) {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                }
            }
        }

        order.setStatus(newStatus);
        return toDetail(order);
    }

    // ---------------------------------------------------------------

    private AdminOrderSummaryResponse toSummary(Order o) {
        User u = o.getUser();
        return new AdminOrderSummaryResponse(
                o.getId(),
                o.getOrderNumber(),
                u.getFirstName() + " " + u.getLastName(),
                u.getEmail(),
                o.getTotalPrice(),
                o.getStatus().name(),
                o.getCreatedAt(),
                o.getItems().size());
    }

    private AdminOrderResponse toDetail(Order o) {
        User u = o.getUser();
        List<OrderItemResponse> items = o.getItems().stream()
                .map(it -> {
                    Product p = it.getProduct();
                    return new OrderItemResponse(
                            it.getId(),
                            p != null ? p.getId() : null,
                            it.getProductName(),
                            p != null ? p.getSlug() : null,
                            null,
                            it.getQuantity(),
                            it.getUnitPrice(),
                            it.getSubtotal());
                })
                .toList();

        return new AdminOrderResponse(
                o.getId(),
                o.getOrderNumber(),
                o.getStatus().name(),
                o.getTotalPrice(),
                o.getCustomerNote(),
                o.getCreatedAt(),
                u.getFirstName() + " " + u.getLastName(),
                u.getEmail(),
                OrderAddressDto.from(o.getAddress()),
                items);
    }
}
