package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank(message = "Ad zorunludur.")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Soyad zorunludur.")
        @Size(max = 100)
        String lastName,

        @Pattern(regexp = "^$|^[0-9+\\s()-]{7,20}$", message = "Geçerli bir telefon numarası giriniz.")
        String phone
) {
}
