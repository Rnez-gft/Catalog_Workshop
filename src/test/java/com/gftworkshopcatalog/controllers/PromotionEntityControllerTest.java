package com.gftworkshopcatalog.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gftworkshopcatalog.exceptions.BadRequest;
import com.gftworkshopcatalog.exceptions.GlobalExceptionHandler;
import com.gftworkshopcatalog.exceptions.InternalServiceException;
import com.gftworkshopcatalog.exceptions.NotFoundPromotion;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PromotionEntityControllerTest {

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
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    @DisplayName("Tests Jackson Config Class")
    void testLocalDateSerialization() throws JsonProcessingException {
        LocalDate date = LocalDate.of(2024, 5, 22);
        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-05-22\"", json);
    }
    @Test
    @DisplayName("List all promotions - Success")
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
                .andExpect(jsonPath("$[0].promotionId").value(promotions.get(0).getPromotionId()))
                .andExpect(jsonPath("$[0].categoryId").value(promotions.get(0).getCategoryId()))
                .andExpect(jsonPath("$[0].discount").value(promotions.get(0).getDiscount()))
                .andExpect(jsonPath("$[0].promotionType").value(promotions.get(0).getPromotionType()))
                .andExpect(jsonPath("$[0].volumeThreshold").value(promotions.get(0).getVolumeThreshold()))
                .andExpect(jsonPath("$[0].startDate").value(promotions.get(0).getStartDate().toString()))
                .andExpect(jsonPath("$[0].endDate").value(promotions.get(0).getEndDate().toString()))
                .andExpect(jsonPath("$[0].isActive").value(promotions.get(0).getIsActive()))
                .andExpect(jsonPath("$[1].promotionId").value(promotions.get(1).getPromotionId()))
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
    @DisplayName("List all promotions - InternalServiceException")
    void listAllPromotions_Failure() throws Exception {
        when(promotionService.findAllPromotions()).thenThrow(new InternalServiceException("Database error"));
        mockMvc.perform(get("/promotions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Database error"));
        verify(promotionService).findAllPromotions();
    }
    @Test
    @DisplayName("Add new promotion - Success")
    void whenPostPromotion_thenReturnCreatedPromotion() throws Exception {
        LocalDate startDate = LocalDate.of(2024,5,22);
        LocalDate endDate = startDate.plusDays(10);
        PromotionEntity promotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);
        when(promotionService.addPromotion(any(PromotionEntity.class))).thenReturn(promotion);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promotion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.promotionId").value(promotion.getPromotionId()))
                .andExpect(jsonPath("$.discount").value(promotion.getDiscount()))
                .andExpect(jsonPath("$.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.endDate").value(endDate.toString()));
    }
    @Test
    @DisplayName("Add new promotion - BadRequest")
    void whenPostPromotion_thenFailDueToInvalidData() throws Exception {
        LocalDate startDate = LocalDate.of(2024,5,22);
        LocalDate endDate = startDate.plusDays(10);
        PromotionEntity promotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);

        when(promotionService.addPromotion(any(PromotionEntity.class))).thenThrow(new BadRequest("Invalid promotion data"));
        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promotion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid promotion data"));
        verify(promotionService).addPromotion(any(PromotionEntity.class));
    }
    @Test
    @DisplayName("Update a promotion - Success")
    void updatePromotion_Success() throws Exception{
        LocalDate startDate = LocalDate.of(2024, 5, 22);
        LocalDate endDate = startDate.plusDays(10);
        LocalDate endDateUpdate = endDate.plusDays(5);
        PromotionEntity existingPromotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);
        PromotionEntity updatedPromotion = new PromotionEntity(1L, 1L, 15.0, "Volume", 5, startDate, endDateUpdate, true);

        when(promotionService.findPromotionById(1L)).thenReturn(existingPromotion);
        when(promotionService.updatePromotion(eq(1L), any(PromotionEntity.class))).thenReturn(updatedPromotion);
        mockMvc.perform(put("/promotions/{id}", updatedPromotion.getPromotionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPromotion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promotionId").value(updatedPromotion.getPromotionId()))
                .andExpect(jsonPath("$.discount").value(updatedPromotion.getDiscount()))
                .andExpect(jsonPath("$.endDate").value(updatedPromotion.getEndDate().toString()));
        verify(promotionService).updatePromotion(eq(1L), any(PromotionEntity.class));
    }
    @Test
    @DisplayName("Update a promotion - NotFoundPromotion")
    void updatePromotion_NotFound() throws Exception {
        Long promotionId = 1L;
        PromotionEntity updatedPromotion = new PromotionEntity(1L, 1L, 15.0, "Volume", 5, LocalDate.now(), LocalDate.now().plusDays(5), true);

        when(promotionService.updatePromotion(eq(promotionId), any(PromotionEntity.class)))
                .thenThrow(new NotFoundPromotion("Promotion not found with ID: " + promotionId));
        mockMvc.perform(put("/promotions/{id}", promotionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPromotion)))
                .andExpect(status().isNotFound())  // Expecting 404 status
                .andExpect(jsonPath("$.message").value("Promotion not found with ID: " + promotionId));
        verify(promotionService).updatePromotion(eq(promotionId), any(PromotionEntity.class));
    }
    @Test
    @DisplayName("Get promotion details by ID - Success")
    void getPromotionById_Success() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 5, 22);
        LocalDate endDate = startDate.plusDays(10);
        PromotionEntity promotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);

        when(promotionService.findPromotionById(promotion.getPromotionId())).thenReturn(promotion);
        mockMvc.perform(get("/promotions/{id}", promotion.getPromotionId())

                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promotionId").value(promotion.getPromotionId()))
                .andExpect(jsonPath("$.categoryId").value(promotion.getCategoryId()))
                .andExpect(jsonPath("$.discount").value(promotion.getDiscount()))
                .andExpect(jsonPath("$.promotionType").value(promotion.getPromotionType()))
                .andExpect(jsonPath("$.volumeThreshold").value(promotion.getVolumeThreshold()))
                .andExpect(jsonPath("$.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.isActive").value(promotion.getIsActive()));

        verify(promotionService).findPromotionById(promotion.getPromotionId());

    }
    @Test
    @DisplayName("Get promotion details by ID - NotFound")
    void getPromotionById_NotFound() throws Exception {
        long promotionId = 999L;

        when(promotionService.findPromotionById(promotionId)).thenThrow(new NotFoundPromotion("Promotion not found with ID: " + promotionId));
        mockMvc.perform(get("/promotions/{id}", promotionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Promotion not found with ID: " + promotionId));
        verify(promotionService).findPromotionById(promotionId);
    }
    @Test
    @DisplayName("Delete a promotion - Success")
    void deletePromotion_Success() throws Exception {
        long promotionId = 1L;

        doNothing().when(promotionService).deletePromotion(promotionId);
        mockMvc.perform(delete("/promotions/{id}", promotionId))
                .andExpect(status().isNoContent());
        verify(promotionService).deletePromotion(promotionId);
    }
    @Test
    @DisplayName("Delete a promotion - NotFoundPromotion")
    void deletePromotion_NotFound() throws Exception {
        long promotionId = 999L;

        doThrow(new NotFoundPromotion("Promotion not found with ID: " + promotionId))
                .when(promotionService).deletePromotion(promotionId);
        mockMvc.perform(delete("/promotions/{id}", promotionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Promotion not found with ID: " + promotionId));
        verify(promotionService).deletePromotion(promotionId);
    }

}
