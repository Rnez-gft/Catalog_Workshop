package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import com.gftworkshopcatalog.services.impl.StatusPromotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatusPromotionServiceImplTest {
    @Mock
    ProductRepository productRepository;
    @Mock
    private PromotionRepository promotionRepository;
    @InjectMocks
    private StatusPromotionServiceImpl statusPromotionServiceImpl;
    private ProductEntity product;
    private PromotionEntity promotionEntity;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

    }
    @Test
    @DisplayName("Get active promotions filters inactive and applies discounts")
    void testGetActivePromotionsFiltersAndApplies() {
        PromotionEntity activePromo = new PromotionEntity(1L, 1L, 0.10, "SEASONAL", 10, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), false);
        PromotionEntity activePromo2 = new PromotionEntity(3L, 2L, 0.25, "SEASONAL", 20, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10), false);
        PromotionEntity inactivePromo = new PromotionEntity(2L, 1L, 0.20, "SEASONAL", 15, LocalDate.now().plusDays(1), LocalDate.now().plusDays(10), false);
        when(promotionRepository.findAll()).thenReturn(Arrays.asList(activePromo, activePromo2, inactivePromo));

        List<PromotionEntity> result = statusPromotionServiceImpl.getActivePromotions();

        verify(promotionRepository).findAll();
        assertEquals(2, result.size(), "Should only include active promotions");
        assertTrue(result.contains(activePromo), "Should include the active promotion");
        assertFalse(result.contains(inactivePromo), "Should not include the inactive promotion");
    }
    @Test
    @DisplayName("Update isActive status based on current date")
    void testUpdateIsActiveStatus() {
        PromotionEntity promotion = new PromotionEntity(1L, 1L, 0.10, "VOLUME", 10, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), false);
        statusPromotionServiceImpl.updateIsActiveStatus(promotion);
        assertTrue(promotion.getIsActive(), "Promotion should be active based on the dates provided");
    }
    @Test
    @DisplayName("Apply active promotions updates product prices")
    void testApplyActivePromotions() {
        PromotionEntity promotion = new PromotionEntity(1L, 1L, 0.20, "SEASONAL", 10, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), true);
        ProductEntity product = new ProductEntity(1L, "Product1", "Description1", 100.0, 1L, 1.0, 100, 10);
        when(productRepository.findByCategoryId(1L)).thenReturn(Collections.singletonList(product));

        statusPromotionServiceImpl.applyActivePromotions(Collections.singletonList(promotion));

        verify(productRepository).findByCategoryId(1L);
        verify(productRepository).save(product);
        assertEquals(80.0, product.getPrice(), "Product price should be reduced by 20%");
    }




}
