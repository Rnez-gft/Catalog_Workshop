package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.OrderDTO;
import com.gftworkshopcatalog.controllers.RelatedProductsController;
import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.ProductService;
import com.gftworkshopcatalog.services.RelatedProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RelatedProductsControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private RelatedProductsService relatedProductsService;

    @InjectMocks
    private RelatedProductsController relatedProductsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get related products - Success")
    void testGetRelatedProducts_Success() {

        long userId = 1L;
        OrderDTO order = new OrderDTO();
        when(relatedProductsService.getLatestOrder(userId)).thenReturn(Optional.of(order));
        List<ProductEntity> relatedProducts = Collections.singletonList(new ProductEntity());
        when(productService.findRelatedProducts(Optional.of(order))).thenReturn(relatedProducts);

        ResponseEntity<List<ProductEntity>> response = relatedProductsController.getRelatedProducts(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(relatedProducts, response.getBody());
        verify(relatedProductsService, times(1)).getLatestOrder(userId);
        verify(productService, times(1)).findRelatedProducts(Optional.of(order));
    }

    @Test
    @DisplayName("Get related products - No Order Found")
    void testGetRelatedProducts_NoOrderFound() {

        long userId = 100L;
        when(relatedProductsService.getLatestOrder(userId)).thenReturn(Optional.empty());

        ResponseEntity<List<ProductEntity>> response = relatedProductsController.getRelatedProducts(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(relatedProductsService, times(1)).getLatestOrder(userId);
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Get related products - Internal Server Error")
    void testGetRelatedProducts_InternalServerError() {

        long userId = 1L;
        when(relatedProductsService.getLatestOrder(userId)).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<List<ProductEntity>> response = relatedProductsController.getRelatedProducts(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(relatedProductsService, times(1)).getLatestOrder(userId);
        verifyNoInteractions(productService);
    }
}