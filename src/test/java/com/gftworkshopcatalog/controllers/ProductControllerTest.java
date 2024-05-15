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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProductControllerTest {
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void test_listAllProducts(){
        Product product1 = new Product(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        Product product2 = new Product(2L,"Building Blocks", "Agent word occur number chair.", 7.89, 2, 1.41, 25, 5);
        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productService.findAllProducts()).thenReturn(mockProducts);
        ResponseEntity<?> responseEntity = productController.listAllProducts();

        assertEquals(mockProducts, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void test_addNewProduct(){
        Product productToAdd = new Product(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        Product addedProduct = new Product(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.addProduct(productToAdd)).thenReturn(addedProduct);

        ResponseEntity<?> responseEntity = productController.addNewProduct(productToAdd);

        assertEquals(addedProduct, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
    @Test
    void test_getProductDetails(){
        long productId = 1L;
        Product product = new Product(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.findProductById(anyLong())).thenReturn(product);

        ResponseEntity<?> responseEntity = productController.getProductDetails(productId);

        assertEquals(product, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void test_updateProduct(){
        long productId = 1L;
        Product updatedProductInput = new Product(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);
        Product updatedProductResult = new Product(1L, "Jacket","Something indicate large central measure watch provide.", 58.79, 1, 3.71, 26, 10);

        when(productService.updateProduct(anyLong(),eq(updatedProductInput))).thenReturn(updatedProductResult);

        ResponseEntity<?> response = productController.updateProduct(productId,updatedProductInput);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductResult,response.getBody());
        assertEquals(updatedProductResult.getName(),updatedProductInput.getName());
        assertEquals(updatedProductResult.getDescription(), updatedProductInput.getDescription());
        assertEquals(updatedProductResult.getPrice(), updatedProductInput.getPrice());
        assertEquals(updatedProductResult.getCategory_Id(), updatedProductInput.getCategory_Id());
        assertEquals(updatedProductResult.getWeight(), updatedProductInput.getWeight());
        assertEquals(updatedProductResult.getWeight(), updatedProductInput.getWeight());
    }
}
