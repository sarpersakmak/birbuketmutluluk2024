package com.birbuketmutluluk.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.birbuketmutluluk.dto.PageResponse;
import com.birbuketmutluluk.dto.ProductDeleteResult;
import com.birbuketmutluluk.dto.ProductRequest;
import com.birbuketmutluluk.dto.ProductResponse;
import com.birbuketmutluluk.dto.ProductSummaryResponse;
import com.birbuketmutluluk.service.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ---- Public ----

    @GetMapping("/api/products")
    public PageResponse<ProductSummaryResponse> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return productService.getProducts(categoryId, q, minPrice, maxPrice, sort, page, size);
    }

    @GetMapping("/api/products/featured")
    public List<ProductSummaryResponse> featured() {
        return productService.getFeatured();
    }

    @GetMapping("/api/products/{slug}")
    public ProductResponse detail(@PathVariable String slug) {
        return productService.getProductBySlug(slug);
    }

    // ---- Admin ----

    @PostMapping("/api/admin/products")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ProductDeleteResult delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
