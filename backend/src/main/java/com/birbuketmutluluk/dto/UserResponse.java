package com.birbuketmutluluk.dto;

import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;

/**
 * Kullanıcı yanıtı. Hassas alan (password) kesinlikle bulunmaz (bölüm 37).
 */
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Role role,
        boolean active
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive()
        );
    }
}
