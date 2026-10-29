package com.birbuketmutluluk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import com.birbuketmutluluk.repository.CartItemRepository;
import com.birbuketmutluluk.repository.OrderRepository;
import com.birbuketmutluluk.repository.ProductImageRepository;
import com.birbuketmutluluk.repository.ProductRepository;

/**
 * Geliştirme verisi (dev): Önceki demo ürünleri (picsum görselli mock veriler) ve
 * bunlara bağlı test sipariş/sepet kayıtlarını bir kez temizler, böylece vitrin
 * temiz kalır. Ürünleri admin panelinden sen ekleyeceksin (kategori opsiyonel).
 */
@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    @Order(3)
    CommandLineRunner cleanDemoData(ProductRepository productRepository,
                                    ProductImageRepository productImageRepository,
                                    OrderRepository orderRepository,
                                    CartItemRepository cartItemRepository) {
        return args -> {
            boolean hasDemo = productImageRepository.findAll().stream()
                    .anyMatch(img -> img.getImageUrl() != null
                            && img.getImageUrl().contains("picsum.photos"));
            if (!hasDemo) {
                return;
            }

            long products = productRepository.count();
            // Sıra önemli (FK): önce sepet öğeleri, sonra siparişler (order_items cascade),
            // en sonda ürünler (product_images cascade).
            cartItemRepository.deleteAll();
            orderRepository.deleteAll();
            productRepository.deleteAll();

            log.warn("Demo veriler temizlendi: {} demo ürün + test siparişleri/sepet öğeleri kaldırıldı. "
                    + "Vitrin artık boş; ürünleri admin panelinden ekleyebilirsiniz.", products);
        };
    }
}
