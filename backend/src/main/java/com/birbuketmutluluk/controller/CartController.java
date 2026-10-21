package com.birbuketmutluluk.controller;

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

import com.birbuketmutluluk.dto.AddCartItemRequest;
import com.birbuketmutluluk.dto.CartResponse;
import com.birbuketmutluluk.dto.UpdateCartItemRequest;
import com.birbuketmutluluk.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication authentication,
                                                @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItem(authentication.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(Authentication authentication,
                                   @PathVariable Long itemId,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(authentication.getName(), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(Authentication authentication, @PathVariable Long itemId) {
        return cartService.removeItem(authentication.getName(), itemId);
    }

    @DeleteMapping
    public CartResponse clear(Authentication authentication) {
        return cartService.clear(authentication.getName());
    }
}
