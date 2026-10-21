package com.birbuketmutluluk.entity;

/**
 * Sipariş durumları (bölüm 17).
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
