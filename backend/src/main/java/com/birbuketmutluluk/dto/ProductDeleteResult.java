package com.birbuketmutluluk.dto;

/**
 * Ürün silme sonucu. hardDeleted=false ise ürün geçmiş siparişlerde
 * yer aldığı için tamamen silinmeyip pasife alınmıştır.
 */
public record ProductDeleteResult(boolean hardDeleted, String message) {
}
