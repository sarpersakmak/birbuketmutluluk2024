package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.birbuketmutluluk.dto.AdminUserResponse;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.OrderRepository;
import com.birbuketmutluluk.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private OrderRepository orderRepository;
    @InjectMocks private AdminUserService adminUserService;

    private User admin;
    private User customer;

    @BeforeEach
    void setup() {
        admin = User.builder().id(1L).email("admin@e.com").role(Role.ADMIN).active(true).build();
        customer = User.builder().id(2L).email("c@e.com").role(Role.USER).active(true).build();
        lenient().when(userRepository.findByEmail("admin@e.com")).thenReturn(Optional.of(admin));
    }

    @Test
    void updateStatus_kendiniPasifleştirme_400() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> adminUserService.updateStatus("admin@e.com", 1L, false))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateStatus_baskaKullaniciyiPasifleştir_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(orderRepository.countByUserId(2L)).thenReturn(3L);

        AdminUserResponse res = adminUserService.updateStatus("admin@e.com", 2L, false);

        assertThat(res.active()).isFalse();
        assertThat(res.orderCount()).isEqualTo(3L);
        assertThat(customer.isActive()).isFalse();
    }
}
