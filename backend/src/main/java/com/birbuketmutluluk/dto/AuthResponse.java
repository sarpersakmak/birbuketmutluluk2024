package com.birbuketmutluluk.dto;

/**
 * Register/Login yanıtı: JWT token + kullanıcı bilgisi.
 */
public record AuthResponse(
        String token,
        UserResponse user
) {
}
