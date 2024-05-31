package com.gftworkshopcatalog.utils;

import com.gftworkshopcatalog.exceptions.InternalServiceException;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.CategoryRepository;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryValidationUtilTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionRepository promotionRepository;

    private CategoryValidationUtils categoryValidationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryValidationUtils = new CategoryValidationUtils(productRepository, promotionRepository);
    }
    @Test
    @DisplayName("Validate category deletion fails due to associated products")
    void testValidateCategoryDeletionFailsDueToProducts() {
        Long categoryId = 1L;
        List<ProductEntity> products = List.of(new ProductEntity());
        List<PromotionEntity> promotions = Collections.emptyList();

        when(productRepository.findByCategoryId(categoryId)).thenReturn(products);
        when(promotionRepository.findByCategoryId(categoryId)).thenReturn(promotions);

        InternalServiceException exception = assertThrows(InternalServiceException.class,
                () -> categoryValidationUtils.validateCategoryDeletion(categoryId),
                "Should throw because category is referenced by products");

        assertEquals("Cannot delete category because it´s referenced by products.", exception.getMessage());
    }
    @Test
    @DisplayName("Validate category deletion fails due to associated promotions")
    void testValidateCategoryDeletionFailsDueToPromotions() {
        Long categoryId = 1L;
        List<ProductEntity> products = Collections.emptyList();
        List<PromotionEntity> promotions = List.of(new PromotionEntity());

        when(productRepository.findByCategoryId(categoryId)).thenReturn(products);
        when(promotionRepository.findByCategoryId(categoryId)).thenReturn(promotions);

        InternalServiceException exception = assertThrows(InternalServiceException.class,
                () -> categoryValidationUtils.validateCategoryDeletion(categoryId),
                "Should throw because category is referenced by promotions");

        assertEquals("Cannot delete category because it´s reference by promotions.", exception.getMessage());
    }
}
