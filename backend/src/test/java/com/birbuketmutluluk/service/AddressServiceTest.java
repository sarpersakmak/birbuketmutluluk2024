package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.birbuketmutluluk.dto.AddressRequest;
import com.birbuketmutluluk.dto.AddressResponse;
import com.birbuketmutluluk.entity.Address;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.AddressRepository;
import com.birbuketmutluluk.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock private AddressRepository addressRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private AddressService addressService;

    private static final String EMAIL = "u@example.com";
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).email(EMAIL).role(Role.USER).active(true).build();
        lenient().when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    }

    private AddressRequest req(Boolean isDefault) {
        return new AddressRequest("Ev", "Ayse", "Yilmaz", "05551112233",
                "İstanbul", "Kadıköy", "Moda", "Cadde No 1", "34710", isDefault);
    }

    @Test
    void create_ilkAdres_otomatikVarsayilanOlur() {
        when(addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(1L)).thenReturn(List.of());
        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));

        AddressResponse res = addressService.create(EMAIL, req(false));

        assertThat(res.isDefault()).isTrue();
        assertThat(res.city()).isEqualTo("İstanbul");
    }

    @Test
    void delete_baskaKullanici_404Firlatir() {
        when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.delete(EMAIL, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
