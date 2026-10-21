package com.birbuketmutluluk.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.PageResponse;
import com.birbuketmutluluk.dto.ProductImageDto;
import com.birbuketmutluluk.dto.ProductRequest;
import com.birbuketmutluluk.dto.ProductResponse;
import com.birbuketmutluluk.dto.ProductSummaryResponse;
import com.birbuketmutluluk.entity.Category;
import com.birbuketmutluluk.entity.Product;
import com.birbuketmutluluk.entity.ProductImage;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.CategoryRepository;
import com.birbuketmutluluk.repository.ProductImageRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.util.SlugGenerator;

import jakarta.persistence.criteria.Predicate;

@Service
public class ProductService {

    private static final int DEFAULT_SIZE = 12;
    private static final int MAX_SIZE = 60;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          ProductImageRepository productImageRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
    }

    // ---------------------------------------------------------------
    //  Public okuma
    // ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> getProducts(Long categoryId, String q,
                                                            BigDecimal minPrice, BigDecimal maxPrice,
                                                            String sort, int page, int size) {
        int safeSize = (size <= 0) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, resolveSort(sort));

        Page<Product> result = productRepository.findAll(buildSpec(categoryId, q, minPrice, maxPrice), pageable);
        List<ProductSummaryResponse> content = toSummaries(result.getContent());

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı."));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getFeatured() {
        return toSummaries(productRepository.findByFeaturedTrueAndAvailableTrueOrderByCreatedAtDesc());
    }

    // ---------------------------------------------------------------
    //  Admin CRUD
    // ---------------------------------------------------------------

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı."));

        Product product = Product.builder()
                .category(category)
                .name(request.name())
                .slug(resolveSlug(request.slug(), request.name(), null))
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .available(request.available() == null || request.available())
                .featured(request.featured() != null && request.featured())
                .customizable(request.customizable() != null && request.customizable())
                .color(request.color())
                .size(request.size())
                .productionTime(request.productionTime())
                .careInfo(request.careInfo())
                .build();

        applyImages(product, request.images());
        productRepository.save(product);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı."));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı."));

        product.setCategory(category);
        product.setName(request.name());
        product.setSlug(resolveSlug(request.slug(), request.name(), id));
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        if (request.available() != null) {
            product.setAvailable(request.available());
        }
        if (request.featured() != null) {
            product.setFeatured(request.featured());
        }
        if (request.customizable() != null) {
            product.setCustomizable(request.customizable());
        }
        product.setColor(request.color());
        product.setSize(request.size());
        product.setProductionTime(request.productionTime());
        product.setCareInfo(request.careInfo());

        // Görselleri tamamen yenile (orphanRemoval eskilerini siler).
        product.clearImages();
        applyImages(product, request.images());

        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı."));
        productRepository.delete(product);
    }

    // ---------------------------------------------------------------
    //  Yardımcılar
    // ---------------------------------------------------------------

    private Specification<Product> buildSpec(Long categoryId, String q,
                                             BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort resolveSort(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "nameAsc" -> Sort.by(Sort.Direction.ASC, "name");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    /**
     * Ürün listesini özet DTO'ya çevirir. Kategori adları ve ana görseller
     * TOPLU (2 ek sorgu) yüklenir; ürün başına sorgu (N+1) yapılmaz.
     */
    private List<ProductSummaryResponse> toSummaries(List<Product> products) {
        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream().map(Product::getId).toList();
        Map<Long, String> mainImages = productImageRepository.findByProductIdInAndIsMainTrue(productIds).stream()
                .collect(Collectors.toMap(pi -> pi.getProduct().getId(), ProductImage::getImageUrl, (a, b) -> a));

        List<Long> categoryIds = products.stream()
                .map(p -> p.getCategory().getId())
                .distinct()
                .toList();
        Map<Long, String> categoryNames = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        return products.stream()
                .map(p -> new ProductSummaryResponse(
                        p.getId(),
                        p.getName(),
                        p.getSlug(),
                        p.getPrice(),
                        p.getStockQuantity(),
                        p.isAvailable(),
                        p.isFeatured(),
                        p.getCategory().getId(),
                        categoryNames.get(p.getCategory().getId()),
                        mainImages.get(p.getId())))
                .toList();
    }

    private void applyImages(Product product, List<ProductImageDto> images) {
        if (images == null) {
            return;
        }
        List<ProductImageDto> valid = images.stream()
                .filter(d -> d.imageUrl() != null && !d.imageUrl().isBlank())
                .toList();
        if (valid.isEmpty()) {
            return;
        }

        boolean anyMain = valid.stream().anyMatch(ProductImageDto::isMain);
        boolean mainAssigned = false;

        for (int i = 0; i < valid.size(); i++) {
            ProductImageDto dto = valid.get(i);
            boolean makeMain = anyMain ? (dto.isMain() && !mainAssigned) : (i == 0);
            if (makeMain) {
                mainAssigned = true;
            }
            ProductImage image = ProductImage.builder()
                    .imageUrl(dto.imageUrl().trim())
                    .displayOrder(dto.displayOrder() != null ? dto.displayOrder() : i)
                    .isMain(makeMain)
                    .build();
            product.addImage(image);
        }
    }

    private String resolveSlug(String provided, String name, Long currentId) {
        String base = SlugGenerator.toSlug(
                (provided != null && !provided.isBlank()) ? provided : name);
        if (base.isBlank()) {
            base = "urun";
        }
        String candidate = base;
        int suffix = 2;
        while (slugTakenByAnother(candidate, currentId)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private boolean slugTakenByAnother(String slug, Long currentId) {
        return productRepository.findBySlug(slug)
                .filter(p -> !p.getId().equals(currentId))
                .isPresent();
    }
}
