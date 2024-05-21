package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.api.dto.PromotionDTO;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class PromotionServiceImplTest {
    @Mock
    private PromotionRepository promotionRepository;
    @InjectMocks
    private PromotionServiceImpl promotionServiceImpl;

    private PromotionEntity promotionEntity;
    private final int promotionId = 1;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        promotionEntity = new PromotionEntity();
        promotionEntity.setId(1L);
        promotionEntity.setCategoryId(1);
        promotionEntity.setDiscount(0.1);
        promotionEntity.setPromotionType("Volume");
        promotionEntity.setVolumeThreshold(10);
        promotionEntity.setStartDate(LocalDate.of(2024, 6,1));
        promotionEntity.setEndDate(LocalDate.of(2024, 8,1));
    }

    @Test
    @DisplayName("Find all promotions")
    void findAllPromotions(){
        PromotionEntity promotion2 = new PromotionEntity();
        promotion2.setCategoryId(3);
        promotion2.setDiscount(0.15);
        promotion2.setPromotionType("Volume");
        promotion2.setVolumeThreshold(15);
        promotion2.setStartDate(LocalDate.of(2024, 11, 12));
        promotion2.setEndDate(LocalDate.of(2024, 12, 12));

        List<PromotionEntity> mockPromotionEntities = Arrays.asList(promotionEntity, promotion2);
        when(promotionRepository.findAll()).thenReturn(mockPromotionEntities);
        List<PromotionEntity> allPromotionEntities = promotionServiceImpl.findAllPromotions();

        assertNotNull(allPromotionEntities, "The promotion list should not be null");
        assertEquals(2, allPromotionEntities.size(),"The promotion list should contain 2 items");
        assertTrue(allPromotionEntities.contains(promotionEntity), "The list should contain 'Promotion 1'");
        assertTrue(allPromotionEntities.contains(promotion2), "The list should contain 'Promotion 2'");
    }

    @Test
    @DisplayName("Should return empty list when no promotions exist")
    void shouldReturnEmptyListWhenNoPromotionsExists(){

        when(promotionRepository.findAll()).thenReturn(Collections.emptyList());

        List<PromotionEntity>allPromotionEntities = promotionServiceImpl.findAllPromotions();

        assertNotNull(allPromotionEntities,"The promotion list should not be null");
        assertTrue(allPromotionEntities.isEmpty(),"The promotion list should be empty");
    }

    @Test
    @DisplayName("Test findAllPromotions handles DataAccessException")
    void testFindAllPromotionsDataAccessException() {
        when(promotionRepository.findAll()).thenThrow(new DataAccessException("Database access error") {});

        Exception exception = assertThrows(RuntimeException.class, () -> promotionServiceImpl.findAllPromotions(),
                "Expected findAllPromotions to throw, but it did not");
        assertTrue(exception.getMessage().contains("Error accessing data from database"));
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertTrue(exception.getCause() instanceof DataAccessException, "The cause should be a DataAccessException");
    }

    @Test
    @DisplayName("Find promotion by ID")
    void test_findPromotionById(){
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotionEntity));

        PromotionEntity foundPromotionEntity = promotionServiceImpl.findPromotiontById(1L);
    }
}
