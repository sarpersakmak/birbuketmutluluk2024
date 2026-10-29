package com.birbuketmutluluk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birbuketmutluluk.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductId(Long productId);
}
