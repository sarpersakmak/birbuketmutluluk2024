package com.birbuketmutluluk.exception;

/**
 * Standart hata yanıtı (bölüm 25).
 */
public record ErrorResponse(
        String timestamp,
        int status,
        String message,
        String path
) {
}
