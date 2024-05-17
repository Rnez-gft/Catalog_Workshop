package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.controllers.ProductController;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductEntityControllerTest {
    @Mock
    private ProductServiceImpl productService;
    @InjectMocks
    private ProductController productController;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void test_listAllProducts(){
        ProductEntity productEntity1 = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        ProductEntity productEntity2 = new ProductEntity(2L,"Building Blocks", "Agent word occur number chair.", 7.89, 2, 1.41, 25, 5);
        List<ProductEntity> mockProductEntities = Arrays.asList(productEntity1, productEntity2);
        when(productService.findAllProducts()).thenReturn(mockProductEntities);
        ResponseEntity<?> responseEntity = productController.listAllProducts();

        assertEquals(mockProductEntities, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @DisplayName("Server Error listAllProducts()")
    @Test
    void test_listAllProducts_InternalServerError() {
        when(productService.findAllProducts()).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.listAllProducts();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    void test_addNewProduct(){
        ProductEntity productEntityToAdd = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        ProductEntity addedProductEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.addProduct(productEntityToAdd)).thenReturn(addedProductEntity);

        ResponseEntity<?> responseEntity = productController.addNewProduct(productEntityToAdd);

        assertEquals(addedProductEntity, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
    @DisplayName("Server Error addNewProduct()")
    @Test
    void test_addNewProduct_InternalServerError() {
        ProductEntity productEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.addProduct(productEntity)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.addNewProduct(productEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    void test_getProductDetails(){
        long productId = 1L;
        ProductEntity productEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.findProductById(anyLong())).thenReturn(productEntity);

        ResponseEntity<?> responseEntity = productController.getProductDetails(productId);

        assertEquals(productEntity, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @DisplayName("Server Error getProductDetails()")
    @Test
    void test_getProductDetails_InternalServerError() {
        long productId = 1L;

        when(productService.findProductById(productId)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.getProductDetails(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    void test_updateProduct(){
        long productId = 1L;
        ProductEntity updatedProductEntityInput = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        ProductEntity updatedProductEntityResult = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.updateProduct(anyLong(),eq(updatedProductEntityInput))).thenReturn(updatedProductEntityResult);

        ResponseEntity<?> response = productController.updateProduct(productId, updatedProductEntityInput);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductEntityResult,response.getBody());
        assertEquals(updatedProductEntityResult.getName(), updatedProductEntityInput.getName());
        assertEquals(updatedProductEntityResult.getDescription(), updatedProductEntityInput.getDescription());
        assertEquals(updatedProductEntityResult.getPrice(), updatedProductEntityInput.getPrice());
        assertEquals(updatedProductEntityResult.getCategory_Id(), updatedProductEntityInput.getCategory_Id());
        assertEquals(updatedProductEntityResult.getWeight(), updatedProductEntityInput.getWeight());
        assertEquals(updatedProductEntityResult.getCurrent_stock(), updatedProductEntityInput.getCurrent_stock());
        assertEquals(updatedProductEntityResult.getMin_stock(), updatedProductEntityInput.getMin_stock());
    }
    @DisplayName("Server Error updateProduct()")
    @Test
    void test_updateProduct_InternalServerError() {
        long productId = 1L;
        ProductEntity updatedProductEntityInput = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        ProductEntity updatedProductEntityResult = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.updateProduct(productId, updatedProductEntityInput)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.updateProduct(productId, updatedProductEntityResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    void test_deleteProduct(){
        long productId = 1L;

        doNothing().when(productService).deleteProduct(anyLong());

        ResponseEntity<?> response = productController.deleteProduct(productId);

        verify(productService, times(1)).deleteProduct(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    @DisplayName("Server Error deleteProduct()")
    @Test
    void test_deleteProduct_InternalServerError() {
        long productId = 1L;

        doThrow(new ServiceException("Internal Server Error")).when(productService).deleteProduct(productId);

        ResponseEntity<?> responseEntity = productController.deleteProduct(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
