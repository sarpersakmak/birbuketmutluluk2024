package com.birbuketmutluluk.dto;

import java.time.LocalDateTime;

import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;

public record AdminUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Role role,
        boolean active,
        LocalDateTime createdAt,
        long orderCount
) {
    public static AdminUserResponse from(User u, long orderCount) {
        return new AdminUserResponse(
                u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(),
                u.getRole(), u.isActive(), u.getCreatedAt(), orderCount);
    }
}
