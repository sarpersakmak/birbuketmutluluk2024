package com.birbuketmutluluk.config;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import com.birbuketmutluluk.entity.Category;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.ProductImage;
import com.birbuketmutluluk.repository.CategoryRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.util.SlugGenerator;

/**
 * Geliştirme için örnek kategori ve ürünleri bir kez oluşturur (bölüm 32).
 * Yalnızca kategori tablosu boşsa çalışır; mevcut veriyi bozmaz.
 *
 * Ürün görselleri geçici placeholder (picsum) adresleridir; gerçek Instagram
 * görselleri sağlandığında admin panelden güncellenebilir.
 */
@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    @Order(2)
    CommandLineRunner seedCatalog(CategoryRepository categoryRepository,
                                  ProductRepository productRepository) {
        return args -> {
            if (categoryRepository.count() > 0) {
                log.info("Katalog zaten dolu, seed atlandi.");
                return;
            }

            Map<String, Category> categories = new LinkedHashMap<>();
            for (String name : new String[]{
                    "Buketler", "Tekli Çiçekler", "Saksı Çiçekleri",
                    "Özel Tasarım", "Hediyelik", "Yeni Ürünler"}) {
                Category c = Category.builder()
                        .name(name)
                        .slug(SlugGenerator.toSlug(name))
                        .description(name + " kategorisi")
                        .active(true)
                        .build();
                categories.put(name, categoryRepository.save(c));
            }

            seedProduct(productRepository, categories.get("Buketler"),
                    "Soğuk Porselen Gül Buketi",
                    "El yapımı soğuk porselen güllerden oluşan zarif buket.",
                    "450.00", 10, true, true, "3-5 gün");

            seedProduct(productRepository, categories.get("Saksı Çiçekleri"),
                    "Mini Çiçek Aranjmanı",
                    "Küçük mekanlar için şık mini aranjman.",
                    "275.00", 15, true, false, "2-3 gün");

            seedProduct(productRepository, categories.get("Buketler"),
                    "Papatya Buketi",
                    "Sade ve sıcak papatya buketi.",
                    "320.00", 0, false, false, "2-4 gün");

            seedProduct(productRepository, categories.get("Özel Tasarım"),
                    "Özel Tasarım Buket",
                    "İstekleriniz doğrultusunda özel hazırlanan buket.",
                    "600.00", 5, true, true, "5-7 gün");

            seedProduct(productRepository, categories.get("Saksı Çiçekleri"),
                    "Saksı Çiçeği",
                    "Uzun ömürlü soğuk porselen saksı çiçeği.",
                    "180.00", 20, false, false, "2-3 gün");

            seedProduct(productRepository, categories.get("Hediyelik"),
                    "Hediyelik Mini Buket",
                    "Küçük sürprizler için ideal hediyelik buket.",
                    "150.00", 25, true, true, "1-2 gün");

            log.info("Ornek katalog olusturuldu: {} kategori, {} urun",
                    categoryRepository.count(), productRepository.count());
        };
    }

    private void seedProduct(ProductRepository productRepository, Category category,
                             String name, String description, String price, int stock,
                             boolean featured, boolean customizable, String productionTime) {
        String slug = SlugGenerator.toSlug(name);
        Product product = Product.builder()
                .category(category)
                .name(name)
                .slug(slug)
                .description(description)
                .price(new BigDecimal(price))
                .stockQuantity(stock)
                .available(true)
                .featured(featured)
                .customizable(customizable)
                .productionTime(productionTime)
                .build();

        ProductImage main = ProductImage.builder()
                .imageUrl("https://picsum.photos/seed/" + slug + "/600/600")
                .displayOrder(0)
                .isMain(true)
                .build();
        ProductImage second = ProductImage.builder()
                .imageUrl("https://picsum.photos/seed/" + slug + "-2/600/600")
                .displayOrder(1)
                .isMain(false)
                .build();
        product.addImage(main);
        product.addImage(second);

        productRepository.save(product);
    }
}
