package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.exceptions.InternalServerError;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
    private ProductServiceImpl productServiceImpl;
    @InjectMocks
    private ProductController productController;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("Find all products - Success")
    void test_listAllProducts(){
        ProductEntity productEntity1 = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);
        ProductEntity productEntity2 = new ProductEntity(2L,"Building Blocks", "Agent word occur number chair.", 7.89, 2L, 1.41, 25, 5);
        List<ProductEntity> mockProductEntities = Arrays.asList(productEntity1, productEntity2);
        when(productServiceImpl.findAllProducts()).thenReturn(mockProductEntities);
        ResponseEntity<?> responseEntity = productController.listAllProducts();

        assertEquals(mockProductEntities, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    @DisplayName("Find all products - InternalServerError")
    void test_listAllProducts_InternalServerError() {
        when(productServiceImpl.findAllProducts()).thenThrow(new InternalServerError("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.listAllProducts();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Internal Server Error", errorResponse.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatus());
    }
    @Test
    @DisplayName("Add a new product - Success")
    void test_addNewProduct(){
        ProductEntity productEntityToAdd = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);
        ProductEntity addedProductEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);

        when(productServiceImpl.addProduct(productEntityToAdd)).thenReturn(addedProductEntity);

        ResponseEntity<?> responseEntity = productController.addNewProduct(productEntityToAdd);

        assertEquals(addedProductEntity, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
    @Test
    @DisplayName("Add a new product - InternalServerError")
    void test_addNewProduct_InternalServerError() {
        ProductEntity product = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);

        when(productServiceImpl.addProduct(product)).thenThrow(new InternalServerError("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.addNewProduct(product);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    @DisplayName("Find product by ID - Success")
    void test_getProductDetails(){
        long productId = 1L;
        ProductEntity productEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);

        when(productServiceImpl.findProductById(anyLong())).thenReturn(productEntity);

        ResponseEntity<?> responseEntity = productController.getProductDetails(productId);

        assertEquals(productEntity, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Find product by ID - NotFoundProduct")
    void testGetProductDetails_NotFound() {
        long productId = 1L;

        when(productServiceImpl.findProductById(productId)).thenThrow(new EntityNotFoundException("Product not found"));

        ResponseEntity<?> response = productController.getProductDetails(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Product not found", ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((ErrorResponse) response.getBody()).getStatus());
    }
    @Test
    @DisplayName("Find product by ID - InternalServerError")
    void test_getProductDetails_InternalServerError() {
        long productId = 1L;

        when(productServiceImpl.findProductById(productId)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.getProductDetails(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    @DisplayName("Update a product - Success")
    void test_updateProduct(){
        long productId = 1L;
        ProductEntity updatedProductInputEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);
        ProductEntity updatedProductResultEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);

        when(productServiceImpl.updateProduct(anyLong(),eq(updatedProductInputEntity))).thenReturn(updatedProductResultEntity);

        ResponseEntity<?> response = productController.updateProduct(productId, updatedProductInputEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductResultEntity,response.getBody());
        assertEquals(updatedProductResultEntity.getName(), updatedProductInputEntity.getName());
        assertEquals(updatedProductResultEntity.getDescription(), updatedProductInputEntity.getDescription());
        assertEquals(updatedProductResultEntity.getPrice(), updatedProductInputEntity.getPrice());
        assertEquals(updatedProductResultEntity.getCategoryId(), updatedProductInputEntity.getCategoryId());
        assertEquals(updatedProductResultEntity.getWeight(), updatedProductInputEntity.getWeight());
        assertEquals(updatedProductResultEntity.getCurrentStock(), updatedProductInputEntity.getCurrentStock());
        assertEquals(updatedProductResultEntity.getMinStock(), updatedProductInputEntity.getMinStock());
    }
    @Test
    @DisplayName("Update a product - InternalServerError")
    void test_updateProduct_InternalServerError() {
        long productId = 1L;
        ProductEntity updatedProductInputEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);
        ProductEntity updatedProductResultEntity = new ProductEntity(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1L, 3.71, 26, 10);

        when(productServiceImpl.updateProduct(productId, updatedProductInputEntity)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = productController.updateProduct(productId, updatedProductResultEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update a product - NotFoundProduct")
    void testUpdateProduct_NotFound() {
        Long productId = 1L;
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);

        when(productServiceImpl.updateProduct(productId, productEntity)).thenThrow(new EntityNotFoundException("Product not found"));

        ResponseEntity<?> response = productController.updateProduct(productId, productEntity);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Product not found", ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((ErrorResponse) response.getBody()).getStatus());
    }
    @Test
    @DisplayName("Delete product - Success")
    void test_deleteProduct(){
        long productId = 1L;

        doNothing().when(productServiceImpl).deleteProduct(anyLong());

        ResponseEntity<?> response = productController.deleteProduct(productId);

        verify(productServiceImpl, times(1)).deleteProduct(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    @Test
    @DisplayName("Delete product - NotFoundProduct")
    void testDeleteProduct_NotFound() {
        long productId = 1L;

        doThrow(new EntityNotFoundException("Product not found")).when(productServiceImpl).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Product not found", ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((ErrorResponse) response.getBody()).getStatus());
    }
    @Test
    @DisplayName("Delete product - InternalServerError")
    void test_deleteProduct_InternalServerError() {
        long productId = 1L;

        doThrow(new ServiceException("Internal Server Error")).when(productServiceImpl).deleteProduct(productId);


        ResponseEntity<?> responseEntity = productController.deleteProduct(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    @DisplayName("Update a product price - Success")
    void testUpdateProductPrice_Success() {
        long productId = 1L;
        double newPrice = 199.99;
        ProductEntity updatedProductEntity = new ProductEntity();
        updatedProductEntity.setId(productId);
        updatedProductEntity.setPrice(newPrice);

        when(productServiceImpl.updateProductPrice(productId, newPrice)).thenReturn(updatedProductEntity);

        ResponseEntity<?> response = productController.updateProductPrice(productId, newPrice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedProductEntity, response.getBody());
    }
    @Test
    @DisplayName("Update a product price - NotFoundProduct")
    void testUpdateProductPrice_NotFound() {
        long productId = 1L;
        double newPrice = 199.99;

        when(productServiceImpl.updateProductPrice(productId, newPrice)).thenThrow(new EntityNotFoundException("Product not found"));

        ResponseEntity<?> response = productController.updateProductPrice(productId, newPrice);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Product not found with ID: "+productId, ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((ErrorResponse) response.getBody()).getStatus());
    }
    @Test
    @DisplayName("Update a product price - InternalServerError")
    void testUpdateProductPrice_InternalServerError() {
        long productId = 1L;
        double newPrice = 199.99;

        when(productServiceImpl.updateProductPrice(productId, newPrice)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<?> response = productController.updateProductPrice(productId, newPrice);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Internal server error", errorResponse.getMessage());
    }
    @Test
    @DisplayName("Update a product stock - Success")
    void testUpdateProductStock_Success() {
        long productId = 1L;
        int newStock = 150;
        ProductEntity updatedProductEntity = new ProductEntity();
        updatedProductEntity.setId(productId);
        updatedProductEntity.setCurrentStock(newStock);

        when(productServiceImpl.updateProductStock(productId, newStock)).thenReturn(updatedProductEntity);

        ResponseEntity<?> response = productController.updateProductStock(productId, newStock);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedProductEntity, response.getBody());
    }
    @Test
    @DisplayName("Update a product stock - NotFoundProduct")
    void testUpdateProductStock_NotFound() {
        long productId = 230;
        int newStock = 150;

        when(productServiceImpl.updateProductStock(productId, newStock)).thenThrow(new NotFoundProduct("Product not found"));

        ResponseEntity<?> response = productController.updateProductStock(productId, newStock);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Product not found with ID: "+ productId, ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((ErrorResponse) response.getBody()).getStatus());
    }
    @Test
    @DisplayName("Update a product stock - InternalServerError")
    void testUpdateProductStock_InternalServerError() {
        long productId = 1L;
        int newStock = 150;

        when(productServiceImpl.updateProductStock(productId, newStock)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<?> response = productController.updateProductStock(productId, newStock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertTrue(((ErrorResponse) response.getBody()).getMessage().contains("Internal server error"));
    }
    @Test
    @DisplayName("Find products by IDs - Success")
    void test_listProductsById_Success() {
        List<Long> ids = Arrays.asList(1L, 2L);
        ProductEntity productEntity1 = new ProductEntity(1L, "Jacket", "A warm winter jacket", 58.79, 1L, 3.71, 26, 10);
        ProductEntity productEntity2 = new ProductEntity(2L, "Building Blocks", "Colorful building blocks", 7.89, 2L, 1.41, 25, 5);
        List<ProductEntity> mockProductEntities = Arrays.asList(productEntity1, productEntity2);

        when(productServiceImpl.findProductsByIds(ids)).thenReturn(mockProductEntities);

        ResponseEntity<?> responseEntity = productController.listProductsById(ids);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockProductEntities, responseEntity.getBody());
    }
    @Test
    @DisplayName("Find products by IDs - NotFoundProduct")
    void test_listProductsById_NotFound() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(productServiceImpl.findProductsByIds(ids)).thenThrow(new EntityNotFoundException("One or more product IDs not found"));

        ResponseEntity<?> responseEntity = productController.listProductsById(ids);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("One or more product IDs not found", errorResponse.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatus());
    }
    @Test
    @DisplayName("Find products by IDs - InternalServerError")
    void test_listProductsById_InternalServerError() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(productServiceImpl.findProductsByIds(ids)).thenThrow(new RuntimeException("Database error occurred while fetching products by IDs"));

        ResponseEntity<?> responseEntity = productController.listProductsById(ids);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Internal server error", errorResponse.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatus());
    }
    @Test
    @DisplayName("Find Price at Checkout - Success")
    void testGetPriceProductCheckout_Success() throws Exception {
        double discountedPrice = 80.0;
        when(productServiceImpl.calculateDiscountedPrice(1L, 5)).thenReturn(discountedPrice);

        ResponseEntity<?> responseEntity = productController.getPriceProductCheckout(1L, 5);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(discountedPrice, responseEntity.getBody());
    }
    @Test
    @DisplayName("Find Price at Checkout - NotFoundProduct")
    void testGetPriceProductCheckout_ProductNotFound() throws Exception {

        when(productServiceImpl.calculateDiscountedPrice(1L, 5)).thenThrow(new NotFoundProduct("Product not found with ID: 1"));

        ResponseEntity<?> responseEntity = productController.getPriceProductCheckout(1L, 5);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Product not found with ID: 1", errorResponse.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatus());
    }
    @Test
    @DisplayName("Find Price at Checkout - InternalServerError")
    void testGetPriceProductCheckout_InternalServerError() throws Exception {
        when(productServiceImpl.calculateDiscountedPrice(1L, 5)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<?> responseEntity = productController.getPriceProductCheckout(1L, 5);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Internal server error", errorResponse.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatus());
    }




}
