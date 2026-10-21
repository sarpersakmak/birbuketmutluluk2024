package com.birbuketmutluluk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank(message = "Adres başlığı zorunludur.")
        @Size(max = 100)
        String title,

        @NotBlank(message = "Ad zorunludur.")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Soyad zorunludur.")
        @Size(max = 100)
        String lastName,

        @Pattern(regexp = "^$|^[0-9+\\s()-]{7,20}$", message = "Geçerli bir telefon numarası giriniz.")
        String phone,

        @NotBlank(message = "İl zorunludur.")
        @Size(max = 100)
        String city,

        @NotBlank(message = "İlçe zorunludur.")
        @Size(max = 100)
        String district,

        @Size(max = 100)
        String neighborhood,

        @NotBlank(message = "Adres detayı zorunludur.")
        @Size(max = 500)
        String addressDetail,

        @Size(max = 20)
        String postalCode,

        Boolean isDefault
) {
}
