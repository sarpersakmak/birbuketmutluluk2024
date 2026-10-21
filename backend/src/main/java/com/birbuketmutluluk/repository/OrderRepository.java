package com.birbuketmutluluk.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birbuketmutluluk.entity.Order;
import com.birbuketmutluluk.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // --- Admin ---

    long countByStatus(OrderStatus status);

    long countByUserId(Long userId);

    @Query("select coalesce(sum(o.totalPrice), 0) from Order o where o.status <> :status")
    BigDecimal sumTotalExcludingStatus(@Param("status") OrderStatus status);

    @Query("""
            select o from Order o
            join fetch o.user
            where (:status is null or o.status = :status)
            order by o.createdAt desc
            """)
    List<Order> findAllForAdmin(@Param("status") OrderStatus status);

    @Query("""
            select distinct o from Order o
            left join fetch o.items i
            left join fetch i.product
            join fetch o.user
            where o.id = :id
            """)
    Optional<Order> findAdminDetailById(@Param("id") Long id);

    @Query("select o.user.id, count(o) from Order o group by o.user.id")
    List<Object[]> countOrdersGroupedByUser();
}
