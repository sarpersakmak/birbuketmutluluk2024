package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import com.birbuketmutluluk.dto.AuthResponse;
import com.birbuketmutluluk.dto.LoginRequest;
import com.birbuketmutluluk.dto.RegisterRequest;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.UserRepository;
import com.birbuketmutluluk.security.JwtService;

/**
 * AuthService birim testleri (bölüm 34) - Mockito, DB gerektirmez.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_yeniEposta_tokenVeKullaniciDoner() {
        RegisterRequest req = new RegisterRequest(
                "Ayse", "Yilmaz", "ayse@example.com", "sifre1234", "05551112233");
        when(userRepository.existsByEmail("ayse@example.com")).thenReturn(false);
        when(passwordEncoder.encode("sifre1234")).thenReturn("HASH");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse res = authService.register(req);

        assertThat(res.token()).isEqualTo("jwt-token");
        assertThat(res.user().email()).isEqualTo("ayse@example.com");
        assertThat(res.user().role()).isEqualTo(Role.USER);
    }

    @Test
    void register_mevcutEposta_conflictFirlatir() {
        RegisterRequest req = new RegisterRequest(
                "Ayse", "Yilmaz", "ayse@example.com", "sifre1234", null);
        when(userRepository.existsByEmail("ayse@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    @Test
    void login_hataliBilgi_unauthorizedFirlatir() {
        LoginRequest req = new LoginRequest("ayse@example.com", "yanlis");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("hatalı");
    }

    @Test
    void login_basarili_tokenDoner() {
        LoginRequest req = new LoginRequest("ayse@example.com", "sifre1234");
        User user = User.builder()
                .id(1L)
                .firstName("Ayse")
                .lastName("Yilmaz")
                .email("ayse@example.com")
                .password("HASH")
                .role(Role.USER)
                .active(true)
                .build();
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("ayse@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse res = authService.login(req);

        assertThat(res.token()).isEqualTo("jwt-token");
        assertThat(res.user().id()).isEqualTo(1L);
    }
}
