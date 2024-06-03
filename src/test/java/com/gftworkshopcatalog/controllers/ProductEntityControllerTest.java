package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.CartProductDTO;
import com.gftworkshopcatalog.exceptions.*;
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
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductEntityControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ProductServiceImpl productServiceImpl;
    @InjectMocks
    private ProductController productController;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Assuming GlobalExceptionHandler handles exceptions
                .build();
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
    void test_listAllProducts_InternalServerError() throws Exception{
        when(productServiceImpl.findAllProducts()).thenThrow(new DatabaseException("Database access failed"));

        mockMvc.perform(get("/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Database access failed"));
    }
    @Test
    @DisplayName("Add a new product - Success")
    void test_addNewProduct(){
        ProductEntity newProduct = new ProductEntity(null, "New Product", "Description", 99.99, 1L, 1.5, 100, 10);
        ProductEntity savedProduct = new ProductEntity(1L, "New Product", "Description", 99.99, 1L, 1.5, 100, 10);
        when(productServiceImpl.addProduct(any(ProductEntity.class))).thenReturn(savedProduct);

        ResponseEntity<ProductEntity> response = productController.addNewProduct(newProduct);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedProduct.getId(), response.getBody().getId());
        assertEquals("New Product", response.getBody().getName());
    }
    @Test
    @DisplayName("Add new product - Bad Request")
    void testAddNewProduct_BadRequest() {
        ProductEntity invalidProduct = new ProductEntity(null, "", null, -10.0, null, -1.0, -1, -5);
        when(productServiceImpl.addProduct(any(ProductEntity.class)))
                .thenThrow(new AddProductInvalidArgumentsExceptions("Invalid product details"));

        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () ->
                productController.addNewProduct(invalidProduct)
        );

        assertNotNull(exception);
        assertEquals("Invalid product details", exception.getMessage());
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
    @DisplayName("Add new product - Internal Server Error")
    void testAddNewProduct_InternalServerError() {
        ProductEntity productToSave = new ProductEntity(null, "New Product", "Description", 99.99, 1L, 1.5, 100, 10);
        when(productServiceImpl.addProduct(any(ProductEntity.class)))
                .thenThrow(new DatabaseException("Database access failed"));

        Exception exception = assertThrows(DatabaseException.class, () ->
                productController.addNewProduct(productToSave)
        );

        assertNotNull(exception);
        assertEquals("Database access failed", exception.getMessage());
    }
    @Test
    @DisplayName("Get product details - Success")
    void testGetProductDetails_Success() {
        long productId = 1L;
        ProductEntity foundProduct = new ProductEntity(productId, "Test Product", "Description", 50.00, 1L, 1.0, 100, 10);
        when(productServiceImpl.findProductById(productId)).thenReturn(foundProduct);

        ResponseEntity<ProductEntity> response = productController.getProductDetails(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    @DisplayName("Get product details - Product Not Found")
    void testGetProductDetails_NotFound() {
        long productId = 1L;
        when(productServiceImpl.findProductById(productId)).thenThrow(new NotFoundProduct("Product not found with ID: " + productId));

        Exception exception = assertThrows(NotFoundProduct.class, () -> productController.getProductDetails(productId));

        assertNotNull(exception);
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
    }
    @Test
    @DisplayName("Get product details - Internal Server Error")
    void test_getProductDetails_InternalServerError() {
        long productId = 1L;
        when(productServiceImpl.findProductById(productId)).thenThrow(new DatabaseException("Database access failed"));

        Exception exception = assertThrows(DatabaseException.class, () -> productController.getProductDetails(productId));

        assertNotNull(exception);
        assertEquals("Database access failed", exception.getMessage());
    }
    @Test
    @DisplayName("Update a product - Success")
    void test_updateProduct(){
        Long productId = 1L;
        ProductEntity productToUpdate = new ProductEntity(productId, "Updated Product", "Updated Description", 55.55, 1L, 1.0, 100, 10);
        ProductEntity updatedProduct = new ProductEntity(productId, "Updated Product", "Updated Description", 55.55, 1L, 1.0, 100, 10);

        when(productServiceImpl.updateProduct(productId, productToUpdate)).thenReturn(updatedProduct);

        ResponseEntity<ProductEntity> response = productController.updateProduct(productId, productToUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());
        assertEquals("Updated Product", response.getBody().getName());
    }
    @Test
    @DisplayName("Update product - Product Not Found")
    void testUpdateProduct_ProductNotFound() {
        Long productId = 1L;
        ProductEntity productToUpdate = new ProductEntity(productId, "Updated Product", "Updated Description", 55.55, 1L, 1.0, 100, 10);

        when(productServiceImpl.updateProduct(productId, productToUpdate)).thenThrow(new NotFoundProduct("Product not found with ID: " + productId));

        NotFoundProduct exception = assertThrows(NotFoundProduct.class, () -> productController.updateProduct(productId, productToUpdate));

        assertNotNull(exception);
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
    }

    @Test
    @DisplayName("Update a product - InternalServerError")
    void test_updateProduct_InternalServerError() {
        Long productId = 1L;
        ProductEntity productToUpdate = new ProductEntity(productId, "Updated Product", "Updated Description", 55.55, 1L, 1.0, 100, 10);

        when(productServiceImpl.updateProduct(productId, productToUpdate)).thenThrow(new DatabaseException("Database access failed"));

        DatabaseException exception = assertThrows(DatabaseException.class, () -> productController.updateProduct(productId, productToUpdate));

        assertNotNull(exception);
        assertEquals("Database access failed", exception.getMessage());
    }

    @Test
    @DisplayName("Delete product - Success")
    void test_deleteProduct(){
        long productId = 1L;
        doNothing().when(productServiceImpl).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productServiceImpl).deleteProduct(productId);
    }
    @Test
    @DisplayName("Delete product - Product Not Found")
    void testDeleteProduct_NotFound() {
        long productId = 1L;
        doThrow(new NotFoundProduct("Product not found with ID: " + productId)).when(productServiceImpl).deleteProduct(productId);

        NotFoundProduct exception = assertThrows(NotFoundProduct.class, () -> productController.deleteProduct(productId));

        assertNotNull(exception);
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
    }

    @Test
    @DisplayName("Delete product - InternalServerError")
    void test_deleteProduct_InternalServerError() {
        long productId = 1L;
        doThrow(new DatabaseException("Database access failed")).when(productServiceImpl).deleteProduct(productId);

        DatabaseException exception = assertThrows(DatabaseException.class, () -> productController.deleteProduct(productId));

        assertNotNull(exception);
        assertEquals("Database access failed", exception.getMessage());
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
        double newPrice = 299.99;
        when(productServiceImpl.updateProductPrice(productId, newPrice)).thenThrow(new NotFoundProduct("Product not found with ID: " + productId));

        NotFoundProduct exception = assertThrows(NotFoundProduct.class,
                () -> productController.updateProductPrice(productId, newPrice),
                "Expected to throw, but it did not");

        assertNotNull(exception);
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
    }
    @Test
    @DisplayName("Update a product price - InternalServerError")
    void testUpdateProductPrice_InternalServerError() {
        long productId = 1L;
        double newPrice = 299.99;
        when(productServiceImpl.updateProductPrice(productId, newPrice)).thenThrow(new DatabaseException("Database access failed"));

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> productController.updateProductPrice(productId, newPrice),
                "Expected to throw, but it did not");

        assertNotNull(exception);
        assertEquals("Database access failed", exception.getMessage());
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
        long productId = 1L;
        int quantity = 10;
        when(productServiceImpl.updateProductStock(productId, quantity)).thenThrow(new NotFoundProduct("Product not found with ID: " + productId));

        Exception exception = assertThrows(NotFoundProduct.class,
                () -> productController.updateProductStock(productId, quantity),
                "Expected to throw, but it did not");

        assertNotNull(exception);
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
    }
    @Test
    @DisplayName("Update a product stock - InternalServerError")
    void testUpdateProductStock_InternalServerError() {
        long productId = 1L;
        int quantity = 10;
        when(productServiceImpl.updateProductStock(productId, quantity)).thenThrow(new ServiceException("Database error occurred", new RuntimeException()));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> productController.updateProductStock(productId, quantity),
                "Expected ServiceException to be thrown");

        assertNotNull(exception);
        assertEquals("Database error occurred", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception.getCause());
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
        List<Long> ids = List.of();
        when(productServiceImpl.findProductsByIds(ids)).thenThrow(new BadRequest("Invalid product IDs provided"));

        Exception exception = assertThrows(BadRequest.class,
                () -> productController.listProductsById(ids),
                "Expected to throw, but it did not");

        assertNotNull(exception);
        assertEquals("Invalid product IDs provided", exception.getMessage());
    }
    @Test
    @DisplayName("Find products by IDs - InternalServerError")
    void test_listProductsById_InternalServerError() {
        List<Long> ids = List.of(1L, 2L);
        when(productServiceImpl.findProductsByIds(ids)).thenThrow(new ServiceException("Database error occurred"));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> productController.listProductsById(ids),
                "Expected ServiceException to be thrown");

        assertNotNull(exception);
        assertEquals("Database error occurred", exception.getMessage());
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
        Long productId = 999L;
        int quantity = 3;
        when(productServiceImpl.calculateDiscountedPrice(productId, quantity)).thenThrow(new NotFoundProduct("Product not found with ID: " + productId));

        NotFoundProduct exception = assertThrows(NotFoundProduct.class,
                () -> productController.getPriceProductCheckout(productId, quantity),
                "Expected NotFoundProduct to be thrown");

        assertNotNull(exception);
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
    }
    @Test
    @DisplayName("Find Price at Checkout - InternalServerError")
    void testGetPriceProductCheckout_InternalServerError() throws Exception {
        Long productId = 1L;
        int quantity = 5;
        when(productServiceImpl.calculateDiscountedPrice(productId, quantity)).thenThrow(new ServiceException("Failed to calculate price due to server error"));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> productController.getPriceProductCheckout(productId, quantity),
                "Expected ServiceException to be thrown");

        assertNotNull(exception);
        assertEquals("Failed to calculate price due to server error", exception.getMessage());
    }

    @Test
    @DisplayName("Get total price at checkout - Success")
    void testGetPriceProductCheckoutV2() {
        List<CartProductDTO> cartProducts = Arrays.asList(
                CartProductDTO.builder()
                        .id(1L)
                        .productId(1L)
                        .productName("Jacket")
                        .productDescription("Something indicate large central measure watch provide.")
                        .quantity(1)
                        .price(new BigDecimal("65.0"))
                        .build(),
                CartProductDTO.builder()
                        .id(2L)
                        .productId(2L)
                        .productName("Building Blocks")
                        .productDescription("Agent word occur number chair.")
                        .quantity(5)
                        .price(new BigDecimal("100.0"))
                        .build()
        );

        List<ProductEntity> discountedProducts = Arrays.asList(
                ProductEntity.builder()
                        .id(1L)
                        .name("Jacket")
                        .price(58.5)
                        .build(),
                ProductEntity.builder()
                        .id(1L)
                        .name("Building Blocks")
                        .price(500.0)
                        .build()
        );

        when(productServiceImpl.calculateListDiscountedPrice(cartProducts)).thenReturn(discountedProducts);

        ResponseEntity<List<ProductEntity>> response = productController.getPriceProductCheckoutV2(cartProducts);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(58.5, response.getBody().get(0).getPrice());
        assertEquals(500.0, response.getBody().get(1).getPrice());
    }

    @Test
    @DisplayName("Get total price at checkout - NotFoundProduct")
    void testGetPriceProductCheckoutV2_ProductNotFound() {
        List<CartProductDTO> cartProducts = Arrays.asList(
                CartProductDTO.builder()
                        .id(1L)
                        .productId(1L)
                        .productName("Jacket")
                        .productDescription("Something indicate large central measure watch provide.")
                        .quantity(1)
                        .price(new BigDecimal("65.0"))
                        .build(),
                CartProductDTO.builder()
                        .id(2L)
                        .productId(2L)
                        .productName("Building Blocks")
                        .productDescription("Agent word occur number chair.")
                        .quantity(5)
                        .price(new BigDecimal("100.0"))
                        .build()
        );

        when(productServiceImpl.calculateListDiscountedPrice(cartProducts)).thenThrow(new NotFoundProduct("Product not found"));

        NotFoundProduct exception = assertThrows(NotFoundProduct.class, () -> {
            productController.getPriceProductCheckoutV2(cartProducts);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Product not found", exception.getMessage());
    }

}
