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

import com.birbuketmutluluk.dto.AddCartItemRequest;
import com.birbuketmutluluk.dto.CartResponse;
import com.birbuketmutluluk.dto.UpdateCartItemRequest;
import com.birbuketmutluluk.entity.Cart;
import com.birbuketmutluluk.entity.CartItem;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.CartItemRepository;
import com.birbuketmutluluk.repository.CartRepository;
import com.birbuketmutluluk.repository.ProductImageRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CartService cartService;

    private static final String EMAIL = "u@example.com";
    private User user;
    private Cart cart;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).email(EMAIL).role(Role.USER).active(true).build();
        cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
        lenient().when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        lenient().when(productImageRepository.findByProductIdInAndIsMainTrue(any())).thenReturn(List.of());
    }

    private Product product(long id, String price, int stock, boolean available) {
        return Product.builder()
                .id(id).name("Ürün " + id).slug("urun-" + id)
                .price(new BigDecimal(price)).stockQuantity(stock).available(available)
                .build();
    }

    @Test
    void addItem_yeniUrun_sepeteEklerVeToplamHesaplar() {
        Product p = product(10L, "100.00", 10, true);
        when(productRepository.findById(10L)).thenReturn(Optional.of(p));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.addItem(EMAIL, new AddCartItemRequest(10L, 2));

        assertThat(res.items()).hasSize(1);
        assertThat(res.totalItems()).isEqualTo(2);
        assertThat(res.totalPrice()).isEqualByComparingTo("200.00");
        assertThat(res.items().get(0).unitPrice()).isEqualByComparingTo("100.00");
    }

    @Test
    void addItem_stokAsiminda_conflictFirlatir() {
        Product p = product(10L, "100.00", 3, true);
        when(productRepository.findById(10L)).thenReturn(Optional.of(p));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(EMAIL, new AddCartItemRequest(10L, 5)))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void addItem_tukenmisUrun_conflictFirlatir() {
        Product p = product(10L, "100.00", 0, true);
        when(productRepository.findById(10L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> cartService.addItem(EMAIL, new AddCartItemRequest(10L, 1)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("tükendi");
    }

    @Test
    void updateItem_baskaKullanicininItemi_forbiddenFirlatir() {
        Cart otherCart = Cart.builder().id(99L).build();
        CartItem foreignItem = CartItem.builder()
                .id(5L).cart(otherCart).product(product(10L, "100.00", 10, true)).quantity(1)
                .unitPrice(new BigDecimal("100.00")).build();
        when(cartItemRepository.findById(5L)).thenReturn(Optional.of(foreignItem));

        assertThatThrownBy(() -> cartService.updateItem(EMAIL, 5L, new UpdateCartItemRequest(2)))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void addItem_mevcutUrun_adetArtirir() {
        Product p = product(10L, "50.00", 10, true);
        CartItem existing = CartItem.builder()
                .id(7L).cart(cart).product(p).quantity(2).unitPrice(new BigDecimal("50.00")).build();
        cart.getItems().add(existing);
        when(productRepository.findById(10L)).thenReturn(Optional.of(p));
        when(cartItemRepository.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.of(existing));

        CartResponse res = cartService.addItem(EMAIL, new AddCartItemRequest(10L, 3));

        assertThat(res.totalItems()).isEqualTo(5);
        assertThat(res.totalPrice()).isEqualByComparingTo("250.00");
    }
}
