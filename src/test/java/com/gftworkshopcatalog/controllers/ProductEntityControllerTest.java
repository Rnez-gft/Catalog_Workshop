package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.controllers.ProductController;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
    @Test
    void test_addNewProduct(){
        ProductEntity productEntityToAdd = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        ProductEntity addedProductEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.addProduct(productEntityToAdd)).thenReturn(addedProductEntity);

        ResponseEntity<?> responseEntity = productController.addNewProduct(productEntityToAdd);

        assertEquals(addedProductEntity, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
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
    @Test
    void test_updateProduct(){
        long productId = 1L;
        ProductEntity updatedProductInputEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        ProductEntity updatedProductResultEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.updateProduct(anyLong(),eq(updatedProductInputEntity))).thenReturn(updatedProductResultEntity);

        ResponseEntity<?> response = productController.updateProduct(productId, updatedProductInputEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductResultEntity,response.getBody());
        assertEquals(updatedProductResultEntity.getName(), updatedProductInputEntity.getName());
        assertEquals(updatedProductResultEntity.getDescription(), updatedProductInputEntity.getDescription());
        assertEquals(updatedProductResultEntity.getPrice(), updatedProductInputEntity.getPrice());
        assertEquals(updatedProductResultEntity.getCategory_Id(), updatedProductInputEntity.getCategory_Id());
        assertEquals(updatedProductResultEntity.getWeight(), updatedProductInputEntity.getWeight());
        assertEquals(updatedProductResultEntity.getCurrent_stock(), updatedProductInputEntity.getCurrent_stock());
        assertEquals(updatedProductResultEntity.getMin_stock(), updatedProductInputEntity.getMin_stock());
    }
    @Test
    void test_deleteProduct(){
        long productId = 1L;

        doNothing().when(productService).deleteProduct(anyLong());

        ResponseEntity<?> response = productController.deleteProduct(productId);

        verify(productService, times(1)).deleteProduct(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
