package com.birbuketmutluluk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sipariş anında kopyalanan teslimat adresi snapshot'ı (bölüm 8 mantığı: geçmiş bozulmasın).
 * Kullanıcı adres defterindeki adresi silse/değiştirse bile sipariş teslimat bilgisi sabit kalır.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddress {

    @Column(name = "ship_title", length = 100)
    private String title;

    @Column(name = "ship_first_name", length = 100)
    private String firstName;

    @Column(name = "ship_last_name", length = 100)
    private String lastName;

    @Column(name = "ship_phone", length = 20)
    private String phone;

    @Column(name = "ship_city", length = 100)
    private String city;

    @Column(name = "ship_district", length = 100)
    private String district;

    @Column(name = "ship_neighborhood", length = 100)
    private String neighborhood;

    @Column(name = "ship_address_detail", length = 500)
    private String addressDetail;

    @Column(name = "ship_postal_code", length = 20)
    private String postalCode;
}
