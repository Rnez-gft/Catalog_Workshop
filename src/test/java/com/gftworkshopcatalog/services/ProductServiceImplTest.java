package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.exceptions.ServiceException;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
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
    private ProductEntity productEntity2;
    private final long productId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productEntity = ProductEntity.builder()
                .id(1L)
                .name("Product 1")
                .description("Product 1 description")
                .price(50.00)
                .categoryId(1L)
                .weight(15.00)
                .currentStock(25)
                .minStock(10)
                .build();

        productEntity2 = ProductEntity.builder()
                .id(2L)
                .name("Product 2")
                .description("Product 2 description")
                .price(60.00)
                .categoryId(2L)
                .weight(25.00)
                .currentStock(50)
                .minStock(15)
                .build();
    }

    @Test
    @DisplayName("Update product stock")
    void updateProductStock_Success() {
        productEntity.setCurrentStock(10);
        int newStock = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductEntity updatedProductEntity = productServiceImpl.updateProductStock(productId, newStock);
        assertEquals(20, updatedProductEntity.getCurrentStock());
        verify(productRepository).save(productEntity);
    }

    @Test
    @DisplayName("Fail to decrement product stock due to insufficient stock")
    void testUpdateProductStock_InsufficientStock() {
        long productId = 1L;
        int quantity = -60; // Attempt to decrement more stock than available

        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(productEntity));

        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> {
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

        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> {
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
        productEntity.setCategoryId(1L);
        productEntity.setWeight(1.0);
        productEntity.setCurrentStock(initialStock);
        productEntity.setMinStock(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        productServiceImpl.updateProductStock(productId, adjustment);

        verify(productRepository, times(1)).save(productEntity);

        assertEquals(initialStock + adjustment, productEntity.getCurrentStock(), "Stock should be correctly adjusted by the specified amount.");
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
        productEntity.setCategoryId(1L);
        productEntity.setWeight(1.0);
        productEntity.setCurrentStock(initialStock);
        productEntity.setMinStock(5);

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

        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> {
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

        Exception exception = assertThrows(NotFoundProduct.class, () -> {
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
        productEntity1.setCategoryId(1L);
        productEntity1.setWeight(15.00);
        productEntity1.setCurrentStock(25);
        productEntity1.setMinStock(10);

        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setName("Product 2");
        productEntity2.setDescription("Product description");
        productEntity2.setPrice(60.00);
        productEntity2.setCategoryId(2L);
        productEntity2.setWeight(25.00);
        productEntity2.setCurrentStock(50);
        productEntity2.setMinStock(15);

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
        // Simulamos que productRepository.findAll() lanza una DataAccessException
        when(productRepository.findAll()).thenThrow(new DataAccessException("Database access error") {});

        // Llamamos a findAllProducts y esperamos que lance una excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            try {
                productServiceImpl.findAllProducts();
            } catch (DataAccessException e) {
                throw new RuntimeException("Error accessing data from database", e);
            }
        }, "Expected findAllProducts to throw a RuntimeException with DataAccessException as cause, but it did not");

        // Verificamos el mensaje de la excepción
        assertTrue(exception.getMessage().contains("Error accessing data from database"),
                "Exception message should contain 'Error accessing data from database'");

        // Verificamos que la causa de la excepción no sea null
        assertNotNull(exception.getCause(), "Cause should not be null");
        // También puedes verificar si la causa es una instancia de DataAccessException si es necesario
        assertInstanceOf(DataAccessException.class, exception.getCause(), "The cause should be a DataAccessException");
    }

    @Test
    @DisplayName("Add product")
    void test_AddProduct (){

        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setId(1L);
        newProductEntity.setName("Product 1");
        newProductEntity.setDescription("Product description");
        newProductEntity.setPrice(50.00);
        newProductEntity.setCategoryId(1L);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrentStock(25);
        newProductEntity.setMinStock(10);

        ProductEntity savedProductEntity = new ProductEntity();
        savedProductEntity.setId(1L);
        savedProductEntity.setName("Product 1");
        savedProductEntity.setDescription("Product description");
        savedProductEntity.setPrice(50.00);
        savedProductEntity.setCategoryId(1L);
        savedProductEntity.setWeight(15.00);
        savedProductEntity.setCurrentStock(25);
        savedProductEntity.setMinStock(10);

        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProductEntity);

        ProductEntity result = productServiceImpl.addProduct(newProductEntity);

        assertNotNull(result, "The saved product should not be null");
        assertEquals(1L, result.getId(), "The product ID should be 1");
        assertEquals("Product 1", result.getName(), "The product name should be 'Product 1'");
        assertEquals("Product description",result.getDescription(),"The product description should be 'Product description' ");
        assertEquals(50.00, result.getPrice(), 0.01, "The price should match the saved value");
        assertEquals(1,result.getCategoryId(),"The category should be 1");
        assertEquals(15.00,result.getWeight(),"The weight should be the saved value");
        assertEquals(25,result.getCurrentStock(),"The current stock should be the saved value");
        assertEquals(10,result.getMinStock(),"The min stock should be the saved value");
    }

    @Test
    @DisplayName("Should handle exception when adding product")
    void shouldHandleExceptionWhenAddingProduct() {
        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setId(1L);
        newProductEntity.setName("Product 1");
        newProductEntity.setDescription("Product description");
        newProductEntity.setPrice(50.00);
        newProductEntity.setCategoryId(1L);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrentStock(25);
        newProductEntity.setMinStock(10);

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
        newProductEntityWithNullFields.setCategoryId(null);
        newProductEntityWithNullFields.setWeight(null);
        newProductEntityWithNullFields.setCurrentStock(null);
        newProductEntityWithNullFields.setMinStock(null);

        AddProductInvalidArgumentsExceptions exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> productServiceImpl.addProduct(newProductEntityWithNullFields));
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
        newProductEntity.setCategoryId(1L);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrentStock(25);
        newProductEntity.setMinStock(10);

        when(productRepository.save(any(ProductEntity.class))).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            productServiceImpl.addProduct(newProductEntity);
        });

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
        productEntity.setCategoryId(1L);
        productEntity.setWeight(15.00);
        productEntity.setCurrentStock(25);
        productEntity.setMinStock(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        ProductEntity foundProductEntity = productServiceImpl.findProductById(productId);

        assertNotNull(foundProductEntity, "The product should not be null");
        assertEquals(productId, foundProductEntity.getId());
        assertEquals("Product description", foundProductEntity.getDescription());
        assertEquals(50.00, foundProductEntity.getPrice());
        assertEquals(1, foundProductEntity.getCategoryId());
        assertEquals(15.00, foundProductEntity.getWeight());
        assertEquals(25, foundProductEntity.getCurrentStock());
        assertEquals(10, foundProductEntity.getMinStock());
    }

    @Test
    @DisplayName("Throw EntityNotFoundException when product not found by ID")
    void test_findProductById_NotFound() {
        long productId = 3L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        NotFoundProduct exception = assertThrows(NotFoundProduct.class, () -> productServiceImpl.findProductById(productId));

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
        existingProductEntity.setCategoryId(1L);
        existingProductEntity.setWeight(15.00);
        existingProductEntity.setCurrentStock(25);
        existingProductEntity.setMinStock(10);

        ProductEntity updatedProductEntityDetails = new ProductEntity();
        updatedProductEntityDetails.setId(2L);
        updatedProductEntityDetails.setName("Product 2");
        updatedProductEntityDetails.setDescription("Product description");
        updatedProductEntityDetails.setPrice(60.00);
        updatedProductEntityDetails.setCategoryId(2L);
        updatedProductEntityDetails.setWeight(25.00);
        updatedProductEntityDetails.setCurrentStock(50);
        updatedProductEntityDetails.setMinStock(15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProductEntity));
        when(productRepository.save(existingProductEntity)).thenReturn(updatedProductEntityDetails);

        ProductEntity updatedProductEntity = productServiceImpl.updateProduct(1L, updatedProductEntityDetails);

        assertEquals(updatedProductEntityDetails.getName(), updatedProductEntity.getName());
        assertEquals(updatedProductEntityDetails.getDescription(), updatedProductEntity.getDescription());
        assertEquals(updatedProductEntityDetails.getPrice(), updatedProductEntity.getPrice());
        assertEquals(updatedProductEntityDetails.getCategoryId(), updatedProductEntity.getCategoryId());
        assertEquals(updatedProductEntityDetails.getWeight(), updatedProductEntity.getWeight());
        assertEquals(updatedProductEntityDetails.getCurrentStock(), updatedProductEntity.getCurrentStock());
        assertEquals(updatedProductEntityDetails.getMinStock(), updatedProductEntity.getMinStock());

        verify(productRepository,times(1)).findById(existingProductEntity.getId());
        verify(productRepository,times(1)).save(existingProductEntity);
    }

    @Test
    @DisplayName("Throw IllegalArgumentException for null product details")
    void testUpdateProduct_NullDetails() {
        Long productId = 1L;

        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> {
            productServiceImpl.updateProduct(productId, null);
        });

        assertEquals("Product details must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Throw RuntimeException when product not found during update")
    void shouldThrowExceptionWhenProductNotFound(){

        long nonExistentProductId = 99L;
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundProduct.class, () -> productServiceImpl.findProductById(nonExistentProductId));

        String expectedMessage = "Product not found with ID: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Throw IllegalArgumentException when product details are null during update")
    void shouldThrowExceptionWhenProductDetailsAreNull() {
        long productId = 1L;

        AddProductInvalidArgumentsExceptions exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> productServiceImpl.updateProduct(productId, null));

        assertEquals("Product details must not be null.", exception.getMessage(), "The exception message should be 'Product details must not be null.'");
    }

    @Test
    @DisplayName("Throw ServiceException when DataAccessException occurs during update")
    void shouldThrowServiceExceptionWhenDataAccessExceptionOccurs() {
        long productId = 1L;
        ProductEntity productEntityDetails = new ProductEntity();

        when(productRepository.findById(productId)).thenReturn(Optional.of(new ProductEntity()));
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

        Exception exception = assertThrows(NotFoundProduct.class, () -> productServiceImpl.deleteProduct(nonExistentProductId));

        String expectedMessage = "Product not found with ID: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Delete Product - Throws IllegalArgumentException for Invalid Product ID")
    void deleteProduct_InvalidProductId_ThrowsIllegalArgumentException() {
        long invalidProductId = -1L;

        Exception exception = assertThrows(NotFoundProduct.class, () -> productServiceImpl.deleteProduct(invalidProductId));

        assertEquals("Product not found with ID: "+ invalidProductId, exception.getMessage());
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating stock")
    void updateProductStock_ProductNotFound_ThrowsRuntimeException() {
        int newStock = 20;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundProduct.class, () -> productServiceImpl.updateProductStock(productId, newStock));

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
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

        Exception exception = assertThrows(AddProductInvalidArgumentsExceptions.class, () -> {
            productServiceImpl.updateProduct(productId, null);
        });

        assertEquals("Product details must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("List of products by id")
    void testListProductsById() {
        // Lista de IDs de productos
        List<Long> listaIds = Arrays.asList(1L, 2L);

        List<ProductEntity> mockProductEntities = Arrays.asList(productEntity, productEntity2);

        when(productRepository.findAllById(listaIds)).thenReturn(mockProductEntities);

        List<ProductEntity> listaDeProductos = productServiceImpl.findProductsByIds(listaIds);

        assertNotNull(listaDeProductos, "La lista de productos no debe ser nula.");
        assertEquals(mockProductEntities.size(), listaDeProductos.size(), "El tamaño de la lista de productos debe coincidir con el número de productos encontrados.");
        assertTrue(listaDeProductos.contains(productEntity), "La lista debe contener 'Product 1'.");
        assertTrue(listaDeProductos.contains(productEntity2), "La lista debe contener 'Product 2'.");
    }

    @Test
    @DisplayName("Throw error when one or more IDs do not exist")
    void testListProductsById_NotFound() {
        List<Long> listaIds = Arrays.asList(1L, 2L, 3L, 4L);

        List<ProductEntity> mockProductEntities = Arrays.asList(productEntity, productEntity2);

        when(productRepository.findAllById(listaIds)).thenReturn(mockProductEntities);

        NotFoundProduct exception = assertThrows(NotFoundProduct.class, () -> {
            productServiceImpl.findProductsByIds(listaIds);
        });

        assertEquals("One or more product IDs not found", exception.getMessage());
    }

    @Test
    @DisplayName("Throw error when database access error occurs")
    void testListProductsById_DatabaseError() {
        List<Long> listaIds = Arrays.asList(1L, 2L, 3L, 4L);

        when(productRepository.findAllById(listaIds)).thenThrow(new DataAccessException("Database error occurred while fetching products by IDs") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productServiceImpl.findProductsByIds(listaIds);
        });

        assertEquals("Database error occurred while fetching products by IDs", exception.getMessage());
    }
}
