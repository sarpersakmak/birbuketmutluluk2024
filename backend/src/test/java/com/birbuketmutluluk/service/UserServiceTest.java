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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.birbuketmutluluk.dto.ChangePasswordRequest;
import com.birbuketmutluluk.dto.UpdateProfileRequest;
import com.birbuketmutluluk.dto.UserResponse;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private static final String EMAIL = "u@example.com";
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L).firstName("Eski").lastName("Isim").email(EMAIL)
                .password("HASH").role(Role.USER).active(true).build();
        lenient().when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    }

    @Test
    void updateProfile_adSoyadTelefonGunceller_epostaRolDegismez() {
        UserResponse res = userService.updateProfile(EMAIL,
                new UpdateProfileRequest("Yeni", "Soyisim", "05551112233"));

        assertThat(res.firstName()).isEqualTo("Yeni");
        assertThat(res.lastName()).isEqualTo("Soyisim");
        assertThat(res.phone()).isEqualTo("05551112233");
        assertThat(res.email()).isEqualTo(EMAIL);   // değişmedi
        assertThat(res.role()).isEqualTo(Role.USER); // değişmedi
    }

    @Test
    void changePassword_yanlisMevcutSifre_400Firlatir() {
        when(passwordEncoder.matches("yanlis", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(EMAIL,
                new ChangePasswordRequest("yanlis", "yenisifre123")))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void changePassword_dogruMevcutSifre_yeniSifreHashlenir() {
        when(passwordEncoder.matches("dogru", "HASH")).thenReturn(true);
        when(passwordEncoder.encode("yenisifre123")).thenReturn("YENI_HASH");

        userService.changePassword(EMAIL, new ChangePasswordRequest("dogru", "yenisifre123"));

        assertThat(user.getPassword()).isEqualTo("YENI_HASH");
    }
}
