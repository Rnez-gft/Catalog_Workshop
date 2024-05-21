package com.gftworkshopcatalog.controllers;


import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

public class PromotionEntityControllerTest {
    @Mock
    private PromotionServiceImpl promotiontServiceImpl;
    @InjectMocks
    private PromotionController promotionController;

    private PromotionEntity promotionEntity;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        promotionEntity = PromotionEntity.builder()
                .id(1L)
                .categoryId(1)
                .discount(0.1)
                .promotionType("Volume")
                .volumeThreshold(10)
                .startDate(LocalDate.of(2024, 6,1))
                .endDate(LocalDate.of(2024, 8,1))
                .build();
    }
}
