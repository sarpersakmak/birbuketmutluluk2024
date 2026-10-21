package com.birbuketmutluluk.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.CategoryRequest;
import com.birbuketmutluluk.dto.CategoryResponse;
import com.birbuketmutluluk.entity.Category;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.CategoryRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.util.SlugGenerator;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.name())
                .slug(resolveSlug(request.slug(), request.name(), null))
                .description(request.description())
                .imageUrl(request.imageUrl())
                .active(request.active() == null || request.active())
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı."));

        category.setName(request.name());
        category.setSlug(resolveSlug(request.slug(), request.name(), id));
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        if (request.active() != null) {
            category.setActive(request.active());
        }
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı."));

        if (productRepository.existsByCategoryId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Bu kategoride ürünler bulunduğu için silinemez. Kategoriyi pasife alabilirsiniz.");
        }
        categoryRepository.delete(category);
    }

    /**
     * Slug çözümü: admin özel slug verdiyse onu, yoksa isimden üretileni kullanır.
     * Benzersizliği garanti eder (çakışmada -2, -3 ... ekler). Güncellemede mevcut kaydı hariç tutar.
     */
    private String resolveSlug(String provided, String name, Long currentId) {
        String base = SlugGenerator.toSlug(
                (provided != null && !provided.isBlank()) ? provided : name);
        if (base.isBlank()) {
            base = "kategori";
        }

        String candidate = base;
        int suffix = 2;
        while (slugTakenByAnother(candidate, currentId)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private boolean slugTakenByAnother(String slug, Long currentId) {
        return categoryRepository.findBySlug(slug)
                .filter(c -> !c.getId().equals(currentId))
                .isPresent();
    }
}
