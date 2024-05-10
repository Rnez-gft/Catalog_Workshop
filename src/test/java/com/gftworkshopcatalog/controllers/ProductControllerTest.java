package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.controllers.ProductController;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_ListAllProducts() {
    	Product product1 = new Product(1L, "Product 1", "Description 1", 10.0, 100L, "Category 1", 0.0, 0.0);
        Product product2 = new Product(2L, "Product 2", "Description 2", 20.0, 1000L, "Category 2", 0.0, 0.0);
        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productService.findAllProducts()).thenReturn(mockProducts);
        
        ResponseEntity<?> responseEntity = productController.listAllProducts();

        assertEquals(mockProducts, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testAddNewProduct() {
    	Product productToAdd = new Product(1L, "Product 1", "Description 1", 10.0, 100L, "Category 1", 0.0, 0.0);
        Product addedProduct = new Product(1L, "Product 1", "Description 1", 10.0, 100L, "Category 1", 0.0, 0.0);
        
        when(productService.addProduct(productToAdd)).thenReturn(addedProduct);

        ResponseEntity<?> responseEntity = productController.addNewProduct(productToAdd);

        assertEquals(addedProduct, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void testGetProductDetails() {
        long productId = 1L;
        Product product = new Product(productId, "Product A", "Description product A", 100.0, 50L, "Category A", 10.0, 2.0);
        when(productService.findProductById(anyLong())).thenReturn(product);

        ResponseEntity<?> responseEntity = productController.getProductDetails(productId);

        assertEquals(product, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testUpdateProduct() {
        long productId = 1L;
        Product updatedProductInput = new Product(productId, "Product updated", "New Description", 120.0, 70L, "Category", 5.0, 2.0);
        Product updatedProductResult = new Product(productId, "Product updated", "New Description", 120.0, 70L, "Category", 5.0, 2.0);

        when(productService.updateProduct(anyLong(), eq(updatedProductInput))).thenReturn(updatedProductResult);

        ResponseEntity<?> response = productController.updateProduct(productId, updatedProductInput);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductResult, response.getBody());
    }

    @Test
    void testDeleteProduct() {
    	long productId = 1L;
    	
        doNothing().when(productService).deleteProduct(anyLong());
        
        ResponseEntity<?> response = productController.deleteProduct(productId);

        verify(productService, times(1)).deleteProduct(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }  
}