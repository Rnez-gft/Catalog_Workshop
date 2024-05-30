package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.InternalServiceException;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.exceptions.NotFoundPromotion;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PromotionServiceImplTest {
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private PromotionServiceImpl promotionServiceImpl;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private ProductEntity product;
    private PromotionEntity promotionEntity;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        product = new ProductEntity();
        product.setId(1L);
        product.setPrice(100.0);
        product.setCategoryId(1L);

        promotionEntity = PromotionEntity.builder()
                .id(1L)
                .categoryId(1L)
                .discount(0.2)
                .promotionType("Volume")
                .volumeThreshold(5)
                .startDate(LocalDate.of(2024, 1,1))
                .endDate(LocalDate.of(2024, 8,1))
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Find all promotions - Success")
    void findAllPromotions(){
        PromotionEntity promotion2 = PromotionEntity.builder()
                .id(2L)
                .categoryId(3L)
                .discount(0.15)
                .promotionType("Volume")
                .volumeThreshold(15)
                .startDate(LocalDate.of(2024, 11, 12))
                .endDate(LocalDate.of(2024, 12, 12))
                .build();

        List<PromotionEntity> mockPromotionEntities = Arrays.asList(promotionEntity, promotion2);
        when(promotionRepository.findAll()).thenReturn(mockPromotionEntities);
        List<PromotionEntity> allPromotionEntities = promotionServiceImpl.findAllPromotions();

        assertNotNull(allPromotionEntities, "The promotion list should not be null");
        assertEquals(2, allPromotionEntities.size(),"The promotion list should contain 2 items");
        assertTrue(allPromotionEntities.contains(promotionEntity), "The list should contain the promotion with the ID: " + promotionEntity.getId());
        assertTrue(allPromotionEntities.contains(promotion2), "The list should contain the promotion with the ID: " + promotion2.getId());
    }
    @Test
    @DisplayName("Find all promotions - Should return empty list when no promotions exist")
    void shouldReturnEmptyListWhenNoPromotionsExists(){

        when(promotionRepository.findAll()).thenReturn(Collections.emptyList());

        List<PromotionEntity>allPromotionEntities = promotionServiceImpl.findAllPromotions();

        assertNotNull(allPromotionEntities,"The promotion list should not be null");
        assertTrue(allPromotionEntities.isEmpty(),"The promotion list should be empty");
    }
    @Test
    @DisplayName("Find all promotions - DataAccessException")
    void testFindAllPromotionsDataAccessException() {
        when(promotionRepository.findAll()).thenThrow(new DataAccessException("Failed to access data") {});

        assertThrows(DataAccessException.class, () -> {
            promotionServiceImpl.findAllPromotions();
        });
    }
    @Test
    @DisplayName("Find promotion by ID - Success")
    void test_findPromotionById(){
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotionEntity));
        PromotionEntity foundPromotionEntity = promotionServiceImpl.findPromotiontById(1L);

        assertNotNull(foundPromotionEntity, "The promotion should not be null");
        assertEquals(promotionEntity.getId(), foundPromotionEntity.getId());
        assertEquals(promotionEntity.getCategoryId(), foundPromotionEntity.getCategoryId());
        assertEquals(promotionEntity.getDiscount(), foundPromotionEntity.getDiscount());
        assertEquals(promotionEntity.getPromotionType(), foundPromotionEntity.getPromotionType());
        assertEquals(promotionEntity.getVolumeThreshold(), foundPromotionEntity.getVolumeThreshold());
        assertEquals(promotionEntity.getStartDate(), foundPromotionEntity.getStartDate());
        assertEquals(promotionEntity.getEndDate(), foundPromotionEntity.getEndDate());
    }

    @Test
    @DisplayName("Find promotion by ID - NotFoundPromotion")
    void test_findPromotionById_NotFound() {
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.empty());

        NotFoundPromotion exception = assertThrows(NotFoundPromotion.class, () -> promotionServiceImpl.findPromotiontById(promotionEntity.getId()));

        assertEquals("Promotion not found with ID: " + promotionEntity.getId(), exception.getMessage(), "The exception message should be 'Promotion not found with ID: " + promotionEntity.getId());
    }

    @Test
    @DisplayName("Delete promotion - Success")
    void test_deletePromotion() {
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.of(promotionEntity));

        promotionServiceImpl.deletePromotion(promotionEntity.getId());

        verify(promotionRepository, times(1)).delete(promotionEntity);
    }
    @Test
    @DisplayName("Delete Promotion - NotFoundPromotion")
    void test_deletePromotion_NotFound() {
        when(promotionRepository.existsById(promotionEntity.getId())).thenReturn(false);

        NotFoundPromotion exception = assertThrows(NotFoundPromotion.class, () -> promotionServiceImpl.deletePromotion(promotionEntity.getId()));

        assertEquals("Promotion not found with ID: " + promotionEntity.getId(), exception.getMessage(), "The exception message should be 'Promotion not found with ID: " + promotionEntity.getId());
    }
    @Test
    @DisplayName("Delete Promotion - InternalServiceException")
    void test_deletePromotion_DataAccessException() {
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.of(promotionEntity));
        doThrow(new InternalServiceException("Failed to delete promotion with ID: " + promotionEntity.getId()) {}).when(promotionRepository).delete(promotionEntity);

        InternalServiceException exception = assertThrows(InternalServiceException.class, () -> promotionServiceImpl.deletePromotion(promotionEntity.getId()));

        assertEquals("Failed to delete promotion with ID: " + promotionEntity.getId(), exception.getMessage(), "The exception message should be 'Failed to delete promotion with ID: " + promotionEntity.getId());
    }
    @Test
    @DisplayName("Add a new promotion - Success")
    void addPromotion_Success(){
        PromotionEntity validPromotion = PromotionEntity.builder()
                .categoryId(1L)
                .discount(0.20)
                .promotionType("Volume")
                .volumeThreshold(10)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        when(promotionRepository.save(any(PromotionEntity.class))).thenReturn(validPromotion);

        PromotionEntity savedPromotion = promotionServiceImpl.addPromotion(validPromotion);

        assertNotNull(savedPromotion);
        assertEquals(0.20, savedPromotion.getDiscount());
        verify(promotionRepository).save(validPromotion);
    }
    @Test
    @DisplayName("Add a new promotion - IllegalArgumentException when promotion details are null")
    void addPromotion_Failure_NullDetails() {
        assertThrows(IllegalArgumentException.class, () -> promotionServiceImpl.addPromotion(null),
                "Expected IllegalArgumentException for null promotion details");
    }
    @Test
    @DisplayName("Add a new promotion - DataIntegrityViolationException")
    void addPromotion_Failure_DataAccessException() {
        when(promotionRepository.save(any(PromotionEntity.class))).thenThrow(new DataIntegrityViolationException("Database error"));

        PromotionEntity validPromotion = new PromotionEntity();
        assertThrows(RuntimeException.class, () -> promotionServiceImpl.addPromotion(validPromotion),
                "Expected RuntimeException when database error occurs");
    }

    @Test
    @DisplayName("Update a promotion - Success")
    void updatePromotion_Success(){
        PromotionEntity updateDetails = PromotionEntity.builder()
                .id(promotionEntity.getId())
                .categoryId(2L)
                .discount(0.15)
                .promotionType("Seasonal")
                .volumeThreshold(20)
                .startDate(LocalDate.of(2024, 5,1))
                .endDate(LocalDate.of(2024, 9,1))
                .build();

        when(promotionRepository.findById(updateDetails.getId())).thenReturn(Optional.of(promotionEntity));
        when(promotionRepository.save(any(PromotionEntity.class))).thenReturn(updateDetails);

        PromotionEntity result = promotionServiceImpl.updatePromotion(updateDetails.getId(), updateDetails);
        assertAll(
                () -> assertEquals(0.15, result.getDiscount()),
                () -> assertEquals(2, result.getCategoryId()),
                () -> assertEquals("Seasonal", result.getPromotionType()),
                () -> assertEquals(20, result.getVolumeThreshold()),
                () -> assertEquals(LocalDate.of(2024, 5, 1), result.getStartDate()),
                () -> assertEquals(LocalDate.of(2024, 9, 1), result.getEndDate())
        );
        verify(promotionRepository).save(promotionEntity);
    }
    @Test
    @DisplayName("Update Promotion - Null Details Exception")
    void testUpdatePromotionNullDetails() {
        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> promotionServiceImpl.updatePromotion(1L, null));
        assertEquals("Product details must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Update a promotion - NotFoundPromotion")
    void updatePromotion_NotFound() {
        PromotionEntity existingPromotion = PromotionEntity.builder()
                .id(promotionEntity.getId())
                .categoryId(1L)
                .discount(0.1)
                .promotionType("Volume")
                .volumeThreshold(10)
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 8, 1))
                .build();
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundPromotion.class,
                () -> promotionServiceImpl.updatePromotion(promotionEntity.getId(), existingPromotion));
    }
    @Test
    @DisplayName("Update a promotion - InternalServiceException")
    void updatePromotion_InternalServiceException() {
        PromotionEntity existingPromotion = PromotionEntity.builder()
                .id(promotionEntity.getId())
                .categoryId(1L)
                .discount(0.1)
                .promotionType("Volume")
                .volumeThreshold(10)
                .startDate(LocalDate.of(2024, 6, 1))
                .endDate(LocalDate.of(2024, 8, 1))
                .build();
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.of(existingPromotion));
        when(promotionRepository.save(any(PromotionEntity.class))).thenThrow(new InternalServiceException("Database failure") {});

        assertThrows(RuntimeException.class,
                () -> promotionServiceImpl.updatePromotion(promotionEntity.getId(), existingPromotion));
    }

    @Test
    @DisplayName("Get active promotion")
     void testGetActivePromotions() {
        PromotionEntity promo1 = new PromotionEntity();
        promo1.setStartDate(LocalDate.now().minusDays(1));
        promo1.setEndDate(LocalDate.now().plusDays(1));
        promo1.setPromotionType("SEASONAL");

        PromotionEntity promo2 = new PromotionEntity();
        promo2.setStartDate(LocalDate.now().minusDays(10));
        promo2.setEndDate(LocalDate.now().minusDays(5));
        promo2.setPromotionType("SEASONAL");

        List<PromotionEntity> promotions = Arrays.asList(promo1, promo2);

        when(promotionRepository.findAll()).thenReturn(promotions);

        List<PromotionEntity> activePromotions = promotionServiceImpl.getActivePromotions();

        assertEquals(1, activePromotions.size());
        assertEquals(promo1, activePromotions.get(0));
    }

    @Test
    @DisplayName("Calculate Discounted Price - NotFoundProduct")
    void testCalculateDiscountedPrice_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundProduct.class, () -> productServiceImpl.calculateDiscountedPrice(1L, 5));
    }

    @Test
    @DisplayName("Calculate Discounted Price - No Active Promotion")
    void testCalculateDiscountedPrice_NoActivePromotion() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promotionRepository.findActivePromotionByCategoryId(1L)).thenReturn(null);

        double price = productServiceImpl.calculateDiscountedPrice(1L, 5);

        assertEquals(100.0, price);
    }

    @Test
    @DisplayName("Calculate Discounted Price - Active Promotion But Not Volume")
    void testCalculateDiscountedPrice_ActivePromotionButNotVolume() {
        promotionEntity.setPromotionType("SEASONAL");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promotionRepository.findActivePromotionByCategoryId(1L)).thenReturn(promotionEntity);

        double price = productServiceImpl.calculateDiscountedPrice(1L, 5);

        assertEquals(100.0, price);
    }

    @Test
    @DisplayName("Calculate Discounted Price - Volume Promotion But Threshold Not Met")
    void testCalculateDiscountedPrice_VolumePromotionButThresholdNotMet() {
        promotionEntity.setIsActive(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promotionRepository.findActivePromotionByCategoryId(1L)).thenReturn(promotionEntity);

        double price = productServiceImpl.calculateDiscountedPrice(1L, 3);

        assertEquals(100.0, price);
    }

    @Test
    @DisplayName("Calculate Discounted Price - Volume Promotion And Threshold Met")
    void testCalculateDiscountedPrice_VolumePromotionThresholdMet() {
        promotionEntity.setIsActive(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promotionRepository.findActivePromotionByCategoryId(1L)).thenReturn(promotionEntity);

        double price = productServiceImpl.calculateDiscountedPrice(1L, 5);

        assertEquals(80.0, price);
    }
}

