package com.birbuketmutluluk.dto;

import com.birbuketmutluluk.entity.OrderAddress;

public record OrderAddressDto(
        String title,
        String firstName,
        String lastName,
        String phone,
        String city,
        String district,
        String neighborhood,
        String addressDetail,
        String postalCode
) {
    public static OrderAddressDto from(OrderAddress a) {
        if (a == null) {
            return null;
        }
        return new OrderAddressDto(
                a.getTitle(), a.getFirstName(), a.getLastName(), a.getPhone(),
                a.getCity(), a.getDistrict(), a.getNeighborhood(), a.getAddressDetail(),
                a.getPostalCode());
    }
}
