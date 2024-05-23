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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void testLocalDateSerialization() throws JsonProcessingException {
        LocalDate date = LocalDate.of(2024, 5, 22);
        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-05-22\"", json);
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

}
