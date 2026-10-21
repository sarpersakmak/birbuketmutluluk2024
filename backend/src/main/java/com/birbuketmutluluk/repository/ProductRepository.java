package com.birbuketmutluluk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.birbuketmutluluk.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /** Detay için kategori + görseller tek sorguda yüklenir (N+1 önlenir). */
    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByCategoryId(Long categoryId);

    List<Product> findByFeaturedTrueAndAvailableTrueOrderByCreatedAtDesc();
}
