package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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

    private PromotionEntity promotionEntity;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        promotionEntity = PromotionEntity.builder()
                .id(1L)
                .categoryId(1L)
                .discount(0.1)
                .promotionType("Volume")
                .volumeThreshold(10)
                .startDate(LocalDate.of(2024, 6,1))
                .endDate(LocalDate.of(2024, 8,1))
                .build();
    }

    @Test
    @DisplayName("Find all promotions: Success")
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
    @DisplayName("Find all promotions: Should return empty list when no promotions exist")
    void shouldReturnEmptyListWhenNoPromotionsExists(){

        when(promotionRepository.findAll()).thenReturn(Collections.emptyList());

        List<PromotionEntity>allPromotionEntities = promotionServiceImpl.findAllPromotions();

        assertNotNull(allPromotionEntities,"The promotion list should not be null");
        assertTrue(allPromotionEntities.isEmpty(),"The promotion list should be empty");
    }
    @Test
    @DisplayName("Find all promotions: Handles DataAccessException")
    void testFindAllPromotionsDataAccessException() {
        when(promotionRepository.findAll()).thenThrow(new DataAccessException("Database access error") {});

        Exception exception = assertThrows(RuntimeException.class, () -> promotionServiceImpl.findAllPromotions(),
                "Expected findAllPromotions to throw, but it did not");
        assertTrue(exception.getMessage().contains("Error accessing data from database"));
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertInstanceOf(DataAccessException.class, exception.getCause(), "The cause should be a DataAccessException");
    }
    @Test
    @DisplayName("Find promotion by ID: Success")
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
    @DisplayName("Find promotion by ID: Throw EntityNotFoundException when promotion not found by ID")
    void test_findPromotionById_NotFound() {
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> promotionServiceImpl.findPromotiontById(promotionEntity.getId()));

        assertEquals("Promotion not found with ID: " + promotionEntity.getId(), exception.getMessage(), "The exception message should be 'Promotion not found with ID: " + promotionEntity.getId());
    }

    @Test
    @DisplayName("Delete Promotion: Success")
    void test_deletePromotion() {
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.of(promotionEntity));

        promotionServiceImpl.deletePromotion(promotionEntity.getId());

        verify(promotionRepository, times(1)).delete(promotionEntity);
    }


    @Test
    @DisplayName("Delete Promotion: Throw EntityNotFoundException when deleting non-existing promotion")
    void test_deletePromotion_NotFound() {
        when(promotionRepository.existsById(promotionEntity.getId())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> promotionServiceImpl.deletePromotion(promotionEntity.getId()));

        assertEquals("Promotion not found with ID: " + promotionEntity.getId(), exception.getMessage(), "The exception message should be 'Promotion not found with ID: " + promotionEntity.getId());
    }
    @Test
    @DisplayName("Delete Promotion: Handle DataAccessException")
    void test_deletePromotion_DataAccessException() {
        when(promotionRepository.findById(promotionEntity.getId())).thenReturn(Optional.of(promotionEntity));
        doThrow(new DataAccessException("...") {}).when(promotionRepository).delete(promotionEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> promotionServiceImpl.deletePromotion(promotionEntity.getId()));

        assertEquals("Failed to delete promotion with ID: " + promotionEntity.getId(), exception.getMessage(), "The exception message should be 'Failed to delete promotion with ID: " + promotionEntity.getId());
    }
    @Test
    @DisplayName("Add a new promotion successfully")
    void addPromotion_Success(){
        PromotionEntity validPromotion = PromotionEntity.builder()
                .categoryId(1L)
                .discount(0.10)
                .promotionType("Volume")
                .volumeThreshold(10)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        when(promotionRepository.save(any(PromotionEntity.class))).thenReturn(validPromotion);

        PromotionEntity savedPromotion = promotionServiceImpl.addPromotion(validPromotion);

        assertNotNull(savedPromotion);
        assertEquals(0.10, savedPromotion.getDiscount());
        verify(promotionRepository).save(validPromotion);
    }
    @Test
    @DisplayName("Fail to add a promotion when promotion details are null")
    void addPromotion_Failure_NullDetails() {
        assertThrows(IllegalArgumentException.class, () -> promotionServiceImpl.addPromotion(null),
                "Expected IllegalArgumentException for null promotion details");
    }
    @Test
    @DisplayName("Handle DataAccessException when adding a promotion")
    void addPromotion_Failure_DataAccessException() {
        when(promotionRepository.save(any(PromotionEntity.class))).thenThrow(new DataIntegrityViolationException("Database error"));

        PromotionEntity validPromotion = new PromotionEntity();
        assertThrows(RuntimeException.class, () -> promotionServiceImpl.addPromotion(validPromotion),
                "Expected RuntimeException when database error occurs");
    }

    @Test
    @DisplayName("Update an existing promotion successfully")
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
    @DisplayName("Throw EntityNotFoundException if promotion not found")
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

        assertThrows(EntityNotFoundException.class,
                () -> promotionServiceImpl.updatePromotion(promotionEntity.getId(), existingPromotion));
    }
    @Test
    @DisplayName("Handle data access exception during promotion update")
    void updatePromotion_DataAccessException() {
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
        when(promotionRepository.save(any(PromotionEntity.class))).thenThrow(new DataAccessException("Database failure") {});

        assertThrows(RuntimeException.class,
                () -> promotionServiceImpl.updatePromotion(promotionEntity.getId(), existingPromotion));
    }

    @Test
    public void testGetActivePromotions() {
        PromotionEntity promo1 = new PromotionEntity();
        promo1.setStartDate(LocalDate.now().minusDays(1));
        promo1.setEndDate(LocalDate.now().plusDays(1));

        PromotionEntity promo2 = new PromotionEntity();
        promo2.setStartDate(LocalDate.now().minusDays(10));
        promo2.setEndDate(LocalDate.now().minusDays(5));

        List<PromotionEntity> promotions = Arrays.asList(promo1, promo2);

        when(promotionRepository.findAll()).thenReturn(promotions);

        List<PromotionEntity> activePromotions = promotionServiceImpl.getActivePromotions();

        assertEquals(1, activePromotions.size());
        assertEquals(promo1, activePromotions.get(0));
    }

}

