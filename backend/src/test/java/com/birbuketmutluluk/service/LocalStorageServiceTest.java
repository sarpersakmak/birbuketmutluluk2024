package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.birbuketmutluluk.exception.ApiException;

class LocalStorageServiceTest {

    private LocalStorageService service;

    @BeforeEach
    void setup() {
        service = new LocalStorageService("build/test-uploads");
        service.init();
    }

    @Test
    void store_bosDosya_400() {
        MockMultipartFile empty = new MockMultipartFile("file", "a.jpg", "image/jpeg", new byte[0]);
        assertThatThrownBy(() -> service.store(empty))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void store_gecersizTip_400() {
        MockMultipartFile txt = new MockMultipartFile("file", "a.txt", "text/plain", "merhaba".getBytes());
        assertThatThrownBy(() -> service.store(txt))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("JPG");
    }
}
