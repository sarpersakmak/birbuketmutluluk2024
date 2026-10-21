package com.birbuketmutluluk.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.AddCartItemRequest;
import com.birbuketmutluluk.dto.CartItemResponse;
import com.birbuketmutluluk.dto.CartResponse;
import com.birbuketmutluluk.dto.UpdateCartItemRequest;
import com.birbuketmutluluk.entity.Cart;
import com.birbuketmutluluk.entity.CartItem;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.ProductImage;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.CartItemRepository;
import com.birbuketmutluluk.repository.CartRepository;
import com.birbuketmutluluk.repository.ProductImageRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       ProductImageRepository productImageRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getCart(String email) {
        return toResponse(getOrCreateCart(resolveUser(email)));
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(resolveUser(email));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı."));

        if (!product.isAvailable() || product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu ürün şu anda satışta değil veya tükendi.");
        }

        CartItem existing = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        int newQuantity = (existing != null ? existing.getQuantity() : 0) + request.quantity();
        if (newQuantity > product.getStockQuantity()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Stok yetersiz. Bu üründen en fazla " + product.getStockQuantity() + " adet eklenebilir.");
        }

        if (existing != null) {
            existing.setQuantity(newQuantity);
            existing.setUnitPrice(product.getPrice());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(resolveUser(email));
        CartItem item = requireOwnedItem(cart, itemId);

        Product product = item.getProduct();
        if (product.getStockQuantity() != null && request.quantity() > product.getStockQuantity()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Stok yetersiz. Bu üründen en fazla " + product.getStockQuantity() + " adet seçebilirsiniz.");
        }

        item.setQuantity(request.quantity());
        item.setUnitPrice(product.getPrice());
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(resolveUser(email));
        CartItem item = requireOwnedItem(cart, itemId);
        cart.getItems().remove(item); // orphanRemoval siler
        return toResponse(cart);
    }

    @Transactional
    public CartResponse clear(String email) {
        Cart cart = getOrCreateCart(resolveUser(email));
        cart.getItems().clear();
        return toResponse(cart);
    }

    // ---------------------------------------------------------------
    //  Yardımcılar
    // ---------------------------------------------------------------

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı."));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    /** Item'ın bu kullanıcının sepetine ait olduğunu doğrular (bölüm 26). */
    private CartItem requireOwnedItem(Cart cart, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sepet öğesi bulunamadı."));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Bu sepet öğesine erişiminiz yok.");
        }
        return item;
    }

    /**
     * Sepeti yanıta çevirir. Birim fiyat ve toplamlar ürünün GÜNCEL fiyatından
     * backend'de hesaplanır; frontend fiyatına asla güvenilmez (bölüm 26-27).
     */
    private CartResponse toResponse(Cart cart) {
        List<CartItem> items = cart.getItems();

        List<Long> productIds = items.stream().map(i -> i.getProduct().getId()).toList();
        Map<Long, String> mainImages = productIds.isEmpty()
                ? Map.of()
                : productImageRepository.findByProductIdInAndIsMainTrue(productIds).stream()
                        .collect(Collectors.toMap(pi -> pi.getProduct().getId(),
                                ProductImage::getImageUrl, (a, b) -> a));

        List<CartItemResponse> itemDtos = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            Product product = item.getProduct();
            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(subtotal);
            totalItems += item.getQuantity();

            itemDtos.add(new CartItemResponse(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    mainImages.get(product.getId()),
                    unitPrice,
                    item.getQuantity(),
                    subtotal,
                    product.isAvailable(),
                    product.getStockQuantity()));
        }

        itemDtos.sort(Comparator.comparing(CartItemResponse::id,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return new CartResponse(cart.getId(), itemDtos, totalItems, totalPrice);
    }
}
