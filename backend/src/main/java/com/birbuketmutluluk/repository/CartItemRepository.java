package com.birbuketmutluluk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birbuketmutluluk.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /** Bir ürünü tüm sepetlerden çıkarır (ürün silinirken FK engelini önler). */
    void deleteByProductId(Long productId);
}
