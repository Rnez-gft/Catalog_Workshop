package com.gftworkshopcatalog.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PromotionEntityControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PromotionServiceImpl promotionService;

    @InjectMocks
    private PromotionController promotionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mockMvc = MockMvcBuilders
                .standaloneSetup(promotionController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }
    @Test
    void testLocalDateSerialization() throws JsonProcessingException {
        LocalDate date = LocalDate.of(2024, 5, 22);
        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-05-22\"", json);
    }
    @Test
    @DisplayName("List all promotions")
    void listAllPromotions_Success() throws Exception {
        LocalDate startDate = LocalDate.of(2024,5,22);
        LocalDate endDate = startDate.plusDays(10);

        LocalDate startDate2 = LocalDate.of(2024,5,22);
        LocalDate endDate2 = startDate.plusDays(10);

        List<PromotionEntity> promotions = Arrays.asList(
                new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true),
                new PromotionEntity(2L, 2L, 15.0, "Seasonal", 10, startDate2, endDate2, true)
        );

        when(promotionService.findAllPromotions()).thenReturn(promotions);

        mockMvc.perform(get("/promotions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(promotions.get(0).getId()))
                .andExpect(jsonPath("$[0].categoryId").value(promotions.get(0).getCategoryId()))
                .andExpect(jsonPath("$[0].discount").value(promotions.get(0).getDiscount()))
                .andExpect(jsonPath("$[0].promotionType").value(promotions.get(0).getPromotionType()))
                .andExpect(jsonPath("$[0].volumeThreshold").value(promotions.get(0).getVolumeThreshold()))
                .andExpect(jsonPath("$[0].startDate").value(promotions.get(0).getStartDate().toString()))
                .andExpect(jsonPath("$[0].endDate").value(promotions.get(0).getEndDate().toString()))
                .andExpect(jsonPath("$[0].isActive").value(promotions.get(0).getIsActive()))
                .andExpect(jsonPath("$[1].id").value(promotions.get(1).getId()))
                .andExpect(jsonPath("$[1].categoryId").value(promotions.get(1).getCategoryId()))
                .andExpect(jsonPath("$[1].discount").value(promotions.get(1).getDiscount()))
                .andExpect(jsonPath("$[1].promotionType").value(promotions.get(1).getPromotionType()))
                .andExpect(jsonPath("$[1].volumeThreshold").value(promotions.get(1).getVolumeThreshold()))
                .andExpect(jsonPath("$[1].startDate").value(promotions.get(1).getStartDate().toString()))
                .andExpect(jsonPath("$[1].endDate").value(promotions.get(1).getEndDate().toString()))
                .andExpect(jsonPath("$[1].isActive").value(promotions.get(1).getIsActive()));

        verify(promotionService).findAllPromotions();
    }
    @Test
    @DisplayName("Successfully creates a promotion")
    void whenPostPromotion_thenReturnCreatedPromotion() throws Exception {
        LocalDate startDate = LocalDate.of(2024,5,22);
        LocalDate endDate = startDate.plusDays(10);
        PromotionEntity promotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);
        when(promotionService.addPromotion(any(PromotionEntity.class))).thenReturn(promotion);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promotion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(promotion.getId()))
                .andExpect(jsonPath("$.discount").value(promotion.getDiscount()))
                .andExpect(jsonPath("$.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.endDate").value(endDate.toString()));
    }
    @Test
    @DisplayName("Successfully updates a promotion")
    void updatePromotion_Success() throws Exception{
        LocalDate startDate = LocalDate.of(2024, 5, 22);
        LocalDate endDate = startDate.plusDays(10);
        LocalDate endDateUpdate = endDate.plusDays(5);
        PromotionEntity existingPromotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);
        PromotionEntity updatedPromotion = new PromotionEntity(1L, 1L, 15.0, "Volume", 5, startDate, endDateUpdate, true);

        when(promotionService.findPromotiontById(1L)).thenReturn(existingPromotion);

        when(promotionService.updatePromotion(eq(1L), any(PromotionEntity.class))).thenReturn(updatedPromotion);

        mockMvc.perform(put("/promotions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPromotion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedPromotion.getId()))
                .andExpect(jsonPath("$.discount").value(updatedPromotion.getDiscount()))
                .andExpect(jsonPath("$.endDate").value(updatedPromotion.getEndDate().toString()));
        verify(promotionService).updatePromotion(eq(1L), any(PromotionEntity.class));
    }

}
