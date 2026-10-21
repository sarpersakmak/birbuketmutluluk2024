package com.birbuketmutluluk.dto;

import com.birbuketmutluluk.entity.Address;

public record AddressResponse(
        Long id,
        String title,
        String firstName,
        String lastName,
        String phone,
        String city,
        String district,
        String neighborhood,
        String addressDetail,
        String postalCode,
        boolean isDefault
) {
    public static AddressResponse from(Address a) {
        return new AddressResponse(
                a.getId(), a.getTitle(), a.getFirstName(), a.getLastName(), a.getPhone(),
                a.getCity(), a.getDistrict(), a.getNeighborhood(), a.getAddressDetail(),
                a.getPostalCode(), a.isDefault());
    }
}
