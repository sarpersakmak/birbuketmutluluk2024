package com.birbuketmutluluk.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birbuketmutluluk.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /** Liste kartları için ana görselleri tek sorguda toplu çeker (N+1 önlenir). */
    List<ProductImage> findByProductIdInAndIsMainTrue(Collection<Long> productIds);

    /** Kart galerisi için tüm görselleri (ana görsel önce, sonra sıraya göre) toplu çeker. */
    List<ProductImage> findByProductIdInOrderByIsMainDescDisplayOrderAsc(Collection<Long> productIds);
}
