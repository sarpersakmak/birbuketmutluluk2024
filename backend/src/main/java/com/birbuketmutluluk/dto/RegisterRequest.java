package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Kayıt isteği. Backend validation frontend'den bağımsız olarak uygulanır (bölüm 24).
 */
public record RegisterRequest(

        @NotBlank(message = "Ad zorunludur.")
        @Size(max = 100, message = "Ad en fazla 100 karakter olabilir.")
        String firstName,

        @NotBlank(message = "Soyad zorunludur.")
        @Size(max = 100, message = "Soyad en fazla 100 karakter olabilir.")
        String lastName,

        @NotBlank(message = "E-posta zorunludur.")
        @Email(message = "Geçerli bir e-posta adresi giriniz.")
        @Size(max = 150, message = "E-posta en fazla 150 karakter olabilir.")
        String email,

        @NotBlank(message = "Şifre zorunludur.")
        @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır.")
        String password,

        @Pattern(regexp = "^$|^[0-9+\\s()-]{7,20}$", message = "Geçerli bir telefon numarası giriniz.")
        String phone
) {
}
