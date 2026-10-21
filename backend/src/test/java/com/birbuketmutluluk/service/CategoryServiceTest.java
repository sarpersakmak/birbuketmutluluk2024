package com.birbuketmutluluk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.birbuketmutluluk.entity.Category;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.CategoryRepository;
import com.birbuketmutluluk.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void delete_urunVarsa_conflictFirlatirVeSilmez() {
        Category category = Category.builder().id(1L).name("Buketler").slug("buketler").active(true).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("pasife")
                .extracting(ex -> ((ApiException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void delete_urunYoksa_siler() {
        Category category = Category.builder().id(2L).name("Boş").slug("bos").active(true).build();
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(2L)).thenReturn(false);

        categoryService.delete(2L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void create_slugUretir() {
        when(categoryRepository.findBySlug(any())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        var res = categoryService.create(new com.birbuketmutluluk.dto.CategoryRequest(
                "Özel Tasarım", null, null, null, true));

        assertThat(res.slug()).isEqualTo("ozel-tasarim");
        assertThat(res.active()).isTrue();
    }
}
