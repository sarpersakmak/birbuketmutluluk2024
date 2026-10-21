package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.birbuketmutluluk.dto.CreateOrderRequest;
import com.birbuketmutluluk.dto.OrderResponse;
import com.birbuketmutluluk.entity.Address;
import com.birbuketmutluluk.entity.Cart;
import com.birbuketmutluluk.entity.CartItem;
import com.birbuketmutluluk.entity.Order;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.AddressRepository;
import com.birbuketmutluluk.repository.CartRepository;
import com.birbuketmutluluk.repository.OrderRepository;
import com.birbuketmutluluk.repository.ProductImageRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private OrderService orderService;

    private static final String EMAIL = "u@example.com";
    private User user;
    private Product product;
    private Cart cart;
    private Address address;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).email(EMAIL).role(Role.USER).active(true).build();
        product = Product.builder()
                .id(10L).name("Gül Buketi").slug("gul-buketi")
                .price(new BigDecimal("100.00")).stockQuantity(5).available(true)
                .build();
        CartItem ci = CartItem.builder().id(1L).product(product).quantity(2)
                .unitPrice(new BigDecimal("100.00")).build();
        cart = Cart.builder().id(1L).user(user).items(new ArrayList<>(List.of(ci))).build();
        address = Address.builder().id(5L).user(user)
                .title("Ev").firstName("Ayse").lastName("Yilmaz").city("İstanbul")
                .district("Kadıköy").addressDetail("...").isDefault(true).build();

        lenient().when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        lenient().when(addressRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(address));
        lenient().when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        lenient().when(productImageRepository.findByProductIdInAndIsMainTrue(any())).thenReturn(List.of());
        lenient().when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(42L);
            return o;
        });
    }

    @Test
    void createOrder_basarili_toplamStokSnapshotVeSepet() {
        OrderResponse res = orderService.createOrder(EMAIL, new CreateOrderRequest(5L, "Doğum günü"));

        // Toplam backend'de DB fiyatından: 2 * 100 = 200
        assertThat(res.totalPrice()).isEqualByComparingTo("200.00");
        // Snapshot
        assertThat(res.items()).hasSize(1);
        assertThat(res.items().get(0).productName()).isEqualTo("Gül Buketi");
        assertThat(res.items().get(0).unitPrice()).isEqualByComparingTo("100.00");
        // Adres snapshot
        assertThat(res.address().city()).isEqualTo("İstanbul");
        // Sipariş no formatı: BBM-YYYYMMDD-0042
        assertThat(res.orderNumber()).matches("BBM-\\d{8}-0042");
        assertThat(res.status()).isEqualTo("PENDING");
        // Stok düştü: 5 - 2 = 3
        assertThat(product.getStockQuantity()).isEqualTo(3);
        // Sepet boşaldı
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void createOrder_bosSepet_conflictFirlatir() {
        cart.getItems().clear();

        assertThatThrownBy(() -> orderService.createOrder(EMAIL, new CreateOrderRequest(5L, null)))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createOrder_yetersizStok_conflictFirlatir() {
        product.setStockQuantity(1); // 2 isteniyor

        assertThatThrownBy(() -> orderService.createOrder(EMAIL, new CreateOrderRequest(5L, null)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("stok");
    }

    @Test
    void createOrder_adresBulunamaz_404Firlatir() {
        when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(EMAIL, new CreateOrderRequest(99L, null)))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
