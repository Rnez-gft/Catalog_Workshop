package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private ProductEntity productEntity;
    private final long productId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Example Product");
        productEntity.setDescription("Description here");
        productEntity.setPrice(100.34);
        productEntity.setCategory_Id(10);
        productEntity.setWeight(10.5);
        productEntity.setCurrent_stock(10);
        productEntity.setMin_stock(10);
    }

    @Test
    @DisplayName("Update product stock")
    void updateProductStock_Success() {
        int newStock = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductEntity updatedProductEntity = productServiceImpl.updateProductStock(productId, newStock);
        assertEquals(20, updatedProductEntity.getCurrent_stock());
        verify(productRepository).save(productEntity);
    }
    @Test
    @DisplayName("Fail to decrement product stock due to insufficient stock")
    void testUpdateProductStock_InsufficientStock() {
        long productId = 1L;
        int quantity = -60; // Attempt to decrement more stock than available

        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(productEntity));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productServiceImpl.updateProductStock(productId, quantity);
        });

        assertEquals("Insufficient stock to decrement by -60", exception.getMessage());
        verify(productRepository, never()).save(productEntity);
    }



    @Test
    @DisplayName("Patch Price Product")
    void testPatchPriceProduct() {
        long productId = 1L;
        double adjustment = 100.00;

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setName("Computadora");
        product.setPrice(adjustment);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productServiceImpl.updateProductPrice(productId, adjustment);

        verify(productRepository, times(1)).save(product);

        assertEquals(adjustment, product.getPrice(), "Price should be updated correctly with the saved value.");
    }

    @Test
    @DisplayName("Price cannot be negative")
    void patchPrice_testIllegalArgumentException() {
        long productId = 1L;
        double negativePrice = -100.00;

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setName("Computadora");
        product.setPrice(0.00);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productServiceImpl.updateProductPrice(productId, negativePrice);
        });

        assertEquals("Price cannot be negative", exception.getMessage());

        verify(productRepository, times(0)).save(product);
    }
    @Test
    @DisplayName("Handles DataAccessException when updating price")
    void updateProductPrice_DataAccessException() {
        long productId = 1L;
        double newPrice = 150.0;

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenThrow(new DataAccessException("Database failure") {});

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productServiceImpl.updateProductPrice(productId, newPrice);
        });

        assertEquals("Failed to update product price for ID: " + productId, exception.getMessage());
        verify(productRepository).save(productEntity);
    }



    @Test
    @DisplayName("Patch Stock Product")
    void testPatchStockProduct() {
        long productId = 1L;
        int initialStock = 10;
        int adjustment = 5;

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setName("Example Product");
        productEntity.setPrice(19.99);
        productEntity.setCategory_Id(1);
        productEntity.setWeight(1.0);
        productEntity.setCurrent_stock(initialStock);
        productEntity.setMin_stock(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        productServiceImpl.updateProductStock(productId, adjustment);

        verify(productRepository, times(1)).save(productEntity);

        assertEquals(initialStock + adjustment, productEntity.getCurrent_stock(), "Stock should be correctly adjusted by the specified amount.");
    }

    @Test
    @DisplayName("Fail to Update Product Stock due to Data Access Issues")
    void testUpdateProductStockDataAccessFailure() {
        long productId = 1L;
        int initialStock = 10;
        int adjustment = 5;

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setName("Example Product");
        productEntity.setPrice(19.99);
        productEntity.setCategory_Id(1);
        productEntity.setWeight(1.0);
        productEntity.setCurrent_stock(initialStock);
        productEntity.setMin_stock(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(productEntity)).thenThrow(new DataAccessException("Data access exception") {});

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productServiceImpl.updateProductStock(productId, adjustment);
        });

        assertEquals("Failed to update product stock for ID: 1", exception.getMessage());
    }


    @Test
    @DisplayName("Throw IllegalArgumentException for negative price")
    void updateProductPrice_NegativePrice_ThrowsIllegalArgumentException() {
        double newPrice = -5.0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productServiceImpl.updateProductPrice(productId, newPrice);
        });

        assertEquals("Price cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating price")
    void updateProductPrice_ProductNotFound_ThrowsRuntimeException() {
        double newPrice = 30.0;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productServiceImpl.updateProductPrice(productId, newPrice);
        });

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Handle server error during product price update")
    void updateProductPrice_ServerError_ThrowsRuntimeException() {
        double newPrice = 25.0;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenThrow(new RuntimeException("Unexpected server error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productServiceImpl.updateProductPrice(productId, newPrice);
        });

        assertEquals("Unexpected server error", exception.getMessage());
        verify(productRepository).save(productEntity);
    }
    @Test
    @DisplayName("Find All Products")
    void test_findAllProducts(){
        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setName("Product 1");
        productEntity1.setDescription("Product description");
        productEntity1.setPrice(50.00);
        productEntity1.setCategory_Id(1);
        productEntity1.setWeight(15.00);
        productEntity1.setCurrent_stock(25);
        productEntity1.setMin_stock(10);

        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setName("Product 2");
        productEntity2.setDescription("Product description");
        productEntity2.setPrice(60.00);
        productEntity2.setCategory_Id(2);
        productEntity2.setWeight(25.00);
        productEntity2.setCurrent_stock(50);
        productEntity2.setMin_stock(15);



        List<ProductEntity> mockProductEntity = Arrays.asList(productEntity1, productEntity2);

        when(productRepository.findAll()).thenReturn(mockProductEntity);

        List<ProductEntity> allProductEntities = productServiceImpl.findAllProducts();

        assertNotNull(allProductEntities, "The product list should not be null");
        assertEquals(2, allProductEntities.size(),"The product list should contain 2 items");
        assertTrue(allProductEntities.contains(productEntity1), "The list should contain 'Product 1'");
        assertTrue(allProductEntities.contains(productEntity2), "The list should contain 'Product 2'");
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProductsExists(){

        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductEntity> allProductEntities = productServiceImpl.findAllProducts();

        assertNotNull(allProductEntities,"The product list should not be null");
        assertTrue(allProductEntities.isEmpty(),"The product list should be empty");
    }

    @Test
    @DisplayName("Test findAllProducts handles DataAccessException")
    void testFindAllProductsDataAccessException() {
        // Given
        when(productRepository.findAll()).thenThrow(new DataAccessException("Database access error") {});


        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.findAllProducts(),
                "Expected findAllProducts to throw, but it did not");
        assertTrue(exception.getMessage().contains("Error accessing data from database"));
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertTrue(exception.getCause() instanceof DataAccessException, "The cause should be a DataAccessException");
    }

    @Test
    @DisplayName("Add product")
    void test_AddProduct (){

        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setId(1L);
        newProductEntity.setName("Product 1");
        newProductEntity.setDescription("Product description");
        newProductEntity.setPrice(50.00);
        newProductEntity.setCategory_Id(1);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrent_stock(25);
        newProductEntity.setMin_stock(10);

        ProductEntity savedProductEntity = new ProductEntity();
        savedProductEntity.setId(1L);
        savedProductEntity.setName("Product 1");
        savedProductEntity.setDescription("Product description");
        savedProductEntity.setPrice(50.00);
        savedProductEntity.setCategory_Id(1);
        savedProductEntity.setWeight(15.00);
        savedProductEntity.setCurrent_stock(25);
        savedProductEntity.setMin_stock(10);

        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProductEntity);

        ProductEntity result = productServiceImpl.addProduct(newProductEntity);

        assertNotNull(result, "The saved product should not be null");
        assertEquals(1L, result.getId(), "The product ID should be 1");
        assertEquals("Product 1", result.getName(), "The product name should be 'Product 1'");
        assertEquals("Product description",result.getDescription(),"The product description should be 'Product description' ");
        assertEquals(50.00, result.getPrice(), 0.01, "The price should match the saved value");
        assertEquals(1,result.getCategory_Id(),"The category should be 1");
        assertEquals(15.00,result.getWeight(),"The weight should be the saved value");
        assertEquals(25,result.getCurrent_stock(),"The current stock should be the saved value");
        assertEquals(10,result.getMin_stock(),"The min stock should be the saved value");
    }

    @Test
    @DisplayName("Should handle exception when adding product")
    void shouldHandleExceptionWhenAddingProduct() {
        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setId(1L);
        newProductEntity.setName("Product 1");
        newProductEntity.setDescription("Product description");
        newProductEntity.setPrice(50.00);
        newProductEntity.setCategory_Id(1);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrent_stock(25);
        newProductEntity.setMin_stock(10);

        when(productRepository.save(any(ProductEntity.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.addProduct(newProductEntity));

        assertEquals("Database error", exception.getMessage(), "The exception message should be 'Database error'");
    }
    @Test
    @DisplayName("Add product should not accept product with null fields")
    void shouldNotAcceptProductWithNullFields() {
        ProductEntity newProductEntityWithNullFields = new ProductEntity();
        newProductEntityWithNullFields.setId(2L);
        newProductEntityWithNullFields.setName(null);
        newProductEntityWithNullFields.setDescription("Product description");
        newProductEntityWithNullFields.setPrice(null);
        newProductEntityWithNullFields.setCategory_Id(null);
        newProductEntityWithNullFields.setWeight(null);
        newProductEntityWithNullFields.setCurrent_stock(null);
        newProductEntityWithNullFields.setMin_stock(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.addProduct(newProductEntityWithNullFields));
        assertEquals("Product details must not be null except description", exception.getMessage(), "The exception message should be 'Product details must not be null except description'");
    }

    @Test
    @DisplayName("Should handle DataAccessException when saving product")
    void shouldHandleDataAccessExceptionWhenSavingProduct() {
        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setId(2L);
        newProductEntity.setName("Product 2");
        newProductEntity.setDescription("Product description");
        newProductEntity.setPrice(50.00);
        newProductEntity.setCategory_Id(1);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrent_stock(25);
        newProductEntity.setMin_stock(10);

        when(productRepository.save(any(ProductEntity.class))).thenThrow(new DataAccessException("Database error") {});


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productServiceImpl.addProduct(newProductEntity);
        });


        assertEquals("Failed to save product", exception.getMessage(), "The exception message should be 'Failed to save product'");
        assertNotNull(exception.getCause(), "The cause of the exception should not be null");
        assertEquals("Database error", exception.getCause().getMessage(), "The cause message should be 'Database error'");
    }
    @Test
    @DisplayName("Find product by ID")
    void test_findProductById(){

        long productId = 2L;
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(2L);
        productEntity.setName("Product 1");
        productEntity.setDescription("Product description");
        productEntity.setPrice(50.00);
        productEntity.setCategory_Id(1);
        productEntity.setWeight(15.00);
        productEntity.setCurrent_stock(25);
        productEntity.setMin_stock(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        ProductEntity foundProductEntity = productServiceImpl.findProductById(productId);

        assertNotNull(foundProductEntity, "The product should not be null");
        assertEquals(productId, foundProductEntity.getId());
        assertEquals("Product description", foundProductEntity.getDescription());
        assertEquals(50.00, foundProductEntity.getPrice());
        assertEquals(1, foundProductEntity.getCategory_Id());
        assertEquals(15.00, foundProductEntity.getWeight());
        assertEquals(25, foundProductEntity.getCurrent_stock());
        assertEquals(10, foundProductEntity.getMin_stock());
    }
    @Test
    @DisplayName("Throw EntityNotFoundException when product not found by ID")
    void test_findProductById_NotFound() {
        long productId = 3L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productServiceImpl.findProductById(productId));

        assertEquals("Product not found with ID: " + productId, exception.getMessage(), "The exception message should be 'Product not found with ID: " + productId);
    }


    @Test
    @DisplayName("Update product")
    void test_updateProduct(){
        ProductEntity existingProductEntity = new ProductEntity();
        existingProductEntity.setId(1L);
        existingProductEntity.setName("Product 1");
        existingProductEntity.setDescription("Product description");
        existingProductEntity.setPrice(50.00);
        existingProductEntity.setCategory_Id(1);
        existingProductEntity.setWeight(15.00);
        existingProductEntity.setCurrent_stock(25);
        existingProductEntity.setMin_stock(10);

        ProductEntity updatedProductEntityDetails = new ProductEntity();
        updatedProductEntityDetails.setId(2L);
        updatedProductEntityDetails.setName("Product 2");
        updatedProductEntityDetails.setDescription("Product description");
        updatedProductEntityDetails.setPrice(60.00);
        updatedProductEntityDetails.setCategory_Id(2);
        updatedProductEntityDetails.setWeight(25.00);
        updatedProductEntityDetails.setCurrent_stock(50);
        updatedProductEntityDetails.setMin_stock(15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProductEntity));
        when(productRepository.save(existingProductEntity)).thenReturn(updatedProductEntityDetails);

        ProductEntity updatedProductEntity = productServiceImpl.updateProduct(1L, updatedProductEntityDetails);

        assertEquals(updatedProductEntityDetails.getName(), updatedProductEntity.getName());
        assertEquals(updatedProductEntityDetails.getDescription(), updatedProductEntity.getDescription());
        assertEquals(updatedProductEntityDetails.getPrice(), updatedProductEntity.getPrice());
        assertEquals(updatedProductEntityDetails.getCategory_Id(), updatedProductEntity.getCategory_Id());
        assertEquals(updatedProductEntityDetails.getWeight(), updatedProductEntity.getWeight());
        assertEquals(updatedProductEntityDetails.getCurrent_stock(), updatedProductEntity.getCurrent_stock());
        assertEquals(updatedProductEntityDetails.getMin_stock(), updatedProductEntity.getMin_stock());

        verify(productRepository,times(1)).findById(existingProductEntity.getId());
        verify(productRepository,times(1)).save(existingProductEntity);
    }
    @Test
    @DisplayName("Throw IllegalArgumentException for null product details")
    void testUpdateProduct_NullDetails() {
        Long productId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productServiceImpl.updateProduct(productId, null);
        });

        assertEquals("Product details must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Throw RuntimeException when product not found during update")
    void shouldThrowExceptionWhenProductNotFound(){

        long nonExistentProductId = 99L;
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.findProductById(nonExistentProductId));

        String expectedMessage = "Product not found with ID: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
    @Test
    @DisplayName("Throw IllegalArgumentException when product details are null during update")
    void shouldThrowExceptionWhenProductDetailsAreNull() {
        long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(new ProductEntity()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.updateProduct(productId, null));

        assertEquals("Product details must not be null.", exception.getMessage(), "The exception message should be 'Product details must not be null.'");
    }
    @Test
    @DisplayName("Throw ServiceException when DataAccessException occurs during update")
    void shouldThrowServiceExceptionWhenDataAccessExceptionOccurs() {
        long productId = 1L;
        ProductEntity productEntityDetails = new ProductEntity(); // Make sure this is correctly initialized

        when(productRepository.findById(productId)).thenReturn(Optional.of(new ProductEntity())); // Fixed to return a non-null entity
        when(productRepository.save(any(ProductEntity.class))).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(ServiceException.class, () -> productServiceImpl.updateProduct(productId, productEntityDetails));

        assertEquals("Failed to update the product with ID: " + productId, exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Delete Product - Success")
    void test_deleteProduct(){
        long productId = 1L;
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setName("Product1");

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        productServiceImpl.deleteProduct(productId);

        verify(productRepository, times(1)).delete(productEntity);
    }
    @Test
    @DisplayName("Delete Product - Throws RuntimeException for Non-Existent Product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {

        long nonExistentProductId = 99L;

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.deleteProduct(nonExistentProductId));

        String expectedMessage = "Product not found with ID: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Delete Product - Throws IllegalArgumentException for Invalid Product ID")
    void deleteProduct_InvalidProductId_ThrowsIllegalArgumentException() {
        long invalidProductId = -1L;

        Exception exception = assertThrows(EntityNotFoundException.class, () -> productServiceImpl.deleteProduct(invalidProductId));

        assertEquals("Product not found with ID: "+ invalidProductId, exception.getMessage());
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating stock")
    void updateProductStock_ProductNotFound_ThrowsRuntimeException() {
        int newStock = 20;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.updateProductStock(productId, newStock));

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(ProductEntity.class));
    }



    @DisplayName("Handles DataAccessException when deleting a product")
    void testDeleteProduct_DataAccessException() {
        long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        doThrow(new DataAccessException("Database failure") {}).when(productRepository).delete(productEntity);

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.deleteProduct(productId),
                "Should throw a RuntimeException for a database failure");

        assertTrue(exception.getMessage().contains("Failed to delete product with ID: " + productId),
                "Exception message should indicate failure to delete due to database error");
        verify(productRepository).delete(productEntity);
    }
    @Test
    @DisplayName("Throw IllegalArgumentException if product entity is null")
    void testUpdateProduct_NullProductEntity() {
        Long productId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productServiceImpl.updateProduct(productId, null);
        });

        assertEquals("Product details must not be null.", exception.getMessage());
    }

}


