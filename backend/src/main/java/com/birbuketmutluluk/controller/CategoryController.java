package com.birbuketmutluluk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.birbuketmutluluk.dto.CategoryRequest;
import com.birbuketmutluluk.dto.CategoryResponse;
import com.birbuketmutluluk.service.CategoryService;

import jakarta.validation.Valid;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ---- Public ----

    @GetMapping("/api/categories")
    public List<CategoryResponse> listActive() {
        return categoryService.getActiveCategories();
    }

    // ---- Admin ----

    @GetMapping("/api/admin/categories")
    public List<CategoryResponse> listAll() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/api/admin/categories")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
