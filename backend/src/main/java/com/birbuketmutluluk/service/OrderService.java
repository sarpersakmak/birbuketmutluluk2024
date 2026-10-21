package com.birbuketmutluluk.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.CreateOrderRequest;
import com.birbuketmutluluk.dto.OrderAddressDto;
import com.birbuketmutluluk.dto.OrderItemResponse;
import com.birbuketmutluluk.dto.OrderResponse;
import com.birbuketmutluluk.dto.OrderSummaryResponse;
import com.birbuketmutluluk.entity.Address;
import com.birbuketmutluluk.entity.Cart;
import com.birbuketmutluluk.entity.CartItem;
import com.birbuketmutluluk.entity.Order;
import com.birbuketmutluluk.entity.OrderAddress;
import com.birbuketmutluluk.entity.OrderItem;
import com.birbuketmutluluk.entity.OrderStatus;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.ProductImage;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.AddressRepository;
import com.birbuketmutluluk.repository.CartRepository;
import com.birbuketmutluluk.repository.OrderRepository;
import com.birbuketmutluluk.repository.ProductImageRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.repository.UserRepository;

@Service
public class OrderService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        AddressRepository addressRepository,
                        ProductRepository productRepository,
                        ProductImageRepository productImageRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Sipariş oluşturma (bölüm 27). Sepetten çalışır; fiyat/stok/toplam backend'de,
     * ürün adı+fiyat snapshot, stok düşer, sepet boşalır — hepsi tek transaction içinde.
     */
    @Transactional
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        User user = resolveUser(email);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "Sepetiniz boş."));
        if (cart.getItems().isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "Sepetiniz boş.");
        }

        Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Teslimat adresi bulunamadı."));

        Order order = Order.builder()
                .user(user)
                // Geçici benzersiz değer (kolona sığacak kadar kısa); id alındıktan sonra gerçeği atanır.
                .orderNumber("TMP-" + UUID.randomUUID().toString().substring(0, 18))
                .status(OrderStatus.PENDING)
                .customerNote(request.customerNote())
                .address(toSnapshot(address))
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı."));

            int quantity = cartItem.getQuantity();

            if (!product.isAvailable()) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "'" + product.getName() + "' şu anda satışta değil.");
            }
            if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "'" + product.getName() + "' için yeterli stok yok.");
            }

            BigDecimal unitPrice = product.getPrice(); // DB fiyatı — frontend'e güvenilmez
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();
            order.addItem(orderItem);

            product.setStockQuantity(product.getStockQuantity() - quantity); // stok düş
        }

        order.setTotalPrice(total);

        Order saved = orderRepository.saveAndFlush(order); // id üretilir
        saved.setOrderNumber(generateOrderNumber(saved.getId()));

        cart.getItems().clear(); // sepeti boşalt

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getMyOrders(String email) {
        User user = resolveUser(email);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(o -> new OrderSummaryResponse(
                        o.getId(),
                        o.getOrderNumber(),
                        o.getStatus().name(),
                        o.getTotalPrice(),
                        o.getItems().size(),
                        o.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String email, Long id) {
        User user = resolveUser(email);
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı."));
        return toResponse(order);
    }

    // ---------------------------------------------------------------
    //  Yardımcılar
    // ---------------------------------------------------------------

    private String generateOrderNumber(Long id) {
        return "BBM-" + LocalDate.now().format(DATE_FMT) + "-" + String.format("%04d", id);
    }

    private OrderAddress toSnapshot(Address a) {
        return OrderAddress.builder()
                .title(a.getTitle())
                .firstName(a.getFirstName())
                .lastName(a.getLastName())
                .phone(a.getPhone())
                .city(a.getCity())
                .district(a.getDistrict())
                .neighborhood(a.getNeighborhood())
                .addressDetail(a.getAddressDetail())
                .postalCode(a.getPostalCode())
                .build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItem> items = order.getItems();

        List<Long> productIds = items.stream()
                .map(i -> i.getProduct() != null ? i.getProduct().getId() : null)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, String> mainImages = productIds.isEmpty()
                ? Map.of()
                : productImageRepository.findByProductIdInAndIsMainTrue(productIds).stream()
                        .collect(Collectors.toMap(pi -> pi.getProduct().getId(),
                                ProductImage::getImageUrl, (a, b) -> a));

        List<OrderItemResponse> itemDtos = items.stream()
                .map(it -> {
                    Product p = it.getProduct();
                    Long pid = (p != null) ? p.getId() : null;
                    String slug = (p != null) ? p.getSlug() : null;
                    String img = (pid != null) ? mainImages.get(pid) : null;
                    return new OrderItemResponse(
                            it.getId(), pid, it.getProductName(), slug, img,
                            it.getQuantity(), it.getUnitPrice(), it.getSubtotal());
                })
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getCustomerNote(),
                order.getCreatedAt(),
                OrderAddressDto.from(order.getAddress()),
                itemDtos);
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı."));
    }
}
