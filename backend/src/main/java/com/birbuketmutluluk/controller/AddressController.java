package com.birbuketmutluluk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birbuketmutluluk.dto.AddressRequest;
import com.birbuketmutluluk.dto.AddressResponse;
import com.birbuketmutluluk.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<AddressResponse> list(Authentication authentication) {
        return addressService.getMyAddresses(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(Authentication authentication,
                                                  @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.create(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public AddressResponse update(Authentication authentication,
                                  @PathVariable Long id,
                                  @Valid @RequestBody AddressRequest request) {
        return addressService.update(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        addressService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
