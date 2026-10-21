package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.birbuketmutluluk.dto.AdminOrderResponse;
import com.birbuketmutluluk.entity.Order;
import com.birbuketmutluluk.entity.OrderAddress;
import com.birbuketmutluluk.entity.OrderItem;
import com.birbuketmutluluk.entity.OrderStatus;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @InjectMocks private AdminOrderService adminOrderService;

    private Order orderWith(OrderStatus status, Product product, int qty) {
        User u = User.builder().id(1L).firstName("Ayse").lastName("Y").email("a@e.com").role(Role.USER).build();
        OrderItem item = OrderItem.builder()
                .id(1L).product(product).productName("Ürün").quantity(qty)
                .unitPrice(new BigDecimal("100.00")).subtotal(new BigDecimal("100.00")).build();
        Order o = Order.builder()
                .id(5L).user(u).orderNumber("BBM-20261019-0005").status(status)
                .totalPrice(new BigDecimal("100.00")).address(OrderAddress.builder().city("Istanbul").build())
                .build();
        o.addItem(item);
        return o;
    }

    @Test
    void updateStatus_gecerliGecis_pendingden_confirmed() {
        Order o = orderWith(OrderStatus.PENDING, null, 1);
        when(orderRepository.findAdminDetailById(5L)).thenReturn(Optional.of(o));

        AdminOrderResponse res = adminOrderService.updateStatus(5L, OrderStatus.CONFIRMED);

        assertThat(res.status()).isEqualTo("CONFIRMED");
        assertThat(o.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void updateStatus_gecersizGecis_deliveredden_conflict() {
        Order o = orderWith(OrderStatus.DELIVERED, null, 1);
        when(orderRepository.findAdminDetailById(5L)).thenReturn(Optional.of(o));

        assertThatThrownBy(() -> adminOrderService.updateStatus(5L, OrderStatus.PREPARING))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void updateStatus_iptal_stokIadeEder() {
        Product product = Product.builder()
                .id(10L).name("Ürün").slug("urun").price(new BigDecimal("100.00"))
                .stockQuantity(3).available(true).build();
        Order o = orderWith(OrderStatus.PENDING, product, 2);
        when(orderRepository.findAdminDetailById(5L)).thenReturn(Optional.of(o));

        adminOrderService.updateStatus(5L, OrderStatus.CANCELLED);

        // 3 + 2 = 5 geri eklendi
        assertThat(product.getStockQuantity()).isEqualTo(5);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateStatus_ayniDurum_conflict() {
        Order o = orderWith(OrderStatus.PENDING, null, 1);
        when(orderRepository.findAdminDetailById(5L)).thenReturn(Optional.of(o));

        assertThatThrownBy(() -> adminOrderService.updateStatus(5L, OrderStatus.PENDING))
                .isInstanceOf(ApiException.class);
    }
}
