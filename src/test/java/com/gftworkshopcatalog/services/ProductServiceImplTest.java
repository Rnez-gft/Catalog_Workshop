package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.Product;
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

    private Product product;
    private final long productId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(productId);
        product.setName("Example Product");
        product.setCurrent_stock(10);
    }
    
    @Test
    @DisplayName("Find All Products")
    void test_findAllProducts(){
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setDescription("Product description");
        product1.setPrice(50.00);
        product1.setCategory_Id(1);
        product1.setWeight(15.00);
        product1.setCurrent_stock(25);
        product1.setMin_stock(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setDescription("Product description");
        product2.setPrice(60.00);
        product2.setCategory_Id(2);
        product2.setWeight(25.00);
        product2.setCurrent_stock(50);
        product2.setMin_stock(15);

        List<Product> mockProduct = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(mockProduct);

        List<Product> allProducts = productServiceImpl.findAllProducts();

        assertNotNull(allProducts, "The product list should not be null");
        assertEquals(2, allProducts.size(),"The product list should contain 2 items");
        assertTrue(allProducts.contains(product1), "The list should contain 'Product 1'");
        assertTrue(allProducts.contains(product2), "The list should contain 'Product 2'");
    }
    
    @Test
    @DisplayName("Should return empty list when no products exist")

    void shouldReturnEmptyListWhenNoProductsExists(){


        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> allProducts = productServiceImpl.findAllProducts();

        assertNotNull(allProducts,"The product list should not be null");
        assertTrue(allProducts.isEmpty(),"The product list should be empty");
    }
    
    @Test
    @DisplayName("Should handle exception when finding all products")
    public void shouldHandleExceptionWhenFindingAllProducts() {
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.findAllProducts());

        assertEquals("Database error", exception.getMessage(), "The exception message should be 'Database error'");
    }
    
    @Test
    @DisplayName("Add product")
    void test_AddProduct (){

        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName("Product 1");
        newProduct.setDescription("Product description");
        newProduct.setPrice(50.00);
        newProduct.setCategory_Id(1);
        newProduct.setWeight(15.00);
        newProduct.setCurrent_stock(25);
        newProduct.setMin_stock(10);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Product 1");
        savedProduct.setDescription("Product description");
        savedProduct.setPrice(50.00);
        savedProduct.setCategory_Id(1);
        savedProduct.setWeight(15.00);
        savedProduct.setCurrent_stock(25);
        savedProduct.setMin_stock(10);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productServiceImpl.addProduct(newProduct);

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
    public void shouldHandleExceptionWhenAddingProduct() {
        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName("Product 1");
        newProduct.setDescription("Product description");
        newProduct.setPrice(50.00);
        newProduct.setCategory_Id(1);
        newProduct.setWeight(15.00);
        newProduct.setCurrent_stock(25);
        newProduct.setMin_stock(10);

        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.addProduct(newProduct));

        assertEquals("Database error", exception.getMessage(), "The exception message should be 'Database error'");
    }
    @Test
    @DisplayName("Should not accept product with null fields")
    public void shouldNotAcceptProductWithNullFields() {
        Product newProductWithNullFields = new Product();
        newProductWithNullFields.setId(2L);
        newProductWithNullFields.setName(null);
        newProductWithNullFields.setDescription("Product description");
        newProductWithNullFields.setPrice(null);
        newProductWithNullFields.setCategory_Id(null);
        newProductWithNullFields.setWeight(null);
        newProductWithNullFields.setCurrent_stock(null);
        newProductWithNullFields.setMin_stock(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.addProduct(newProductWithNullFields));
        assertEquals("Product details must not be null except description", exception.getMessage(), "The exception message should be 'Product details must not be null except description'");
    }
    @Test
    @DisplayName("Should not accept product with negative values")
    public void shouldNotAcceptProductWithNegativeValues() {

        Product newProductWithNegativeValues = getProduct();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.addProduct(newProductWithNegativeValues));
        assertEquals("Product details must not contain negative values", exception.getMessage(), "The exception message should be 'Product details must not contain negative values'");
    }

    private static Product getProduct() {
        Product newProductWithNegativeValues = new Product();
        newProductWithNegativeValues.setId(3L);
        newProductWithNegativeValues.setName("Product 3");
        newProductWithNegativeValues.setDescription("Product description");
        newProductWithNegativeValues.setPrice(-50.00);
        newProductWithNegativeValues.setCategory_Id(1);
        newProductWithNegativeValues.setWeight(-15.00);
        newProductWithNegativeValues.setCurrent_stock(-25);
        newProductWithNegativeValues.setMin_stock(-10);
        return newProductWithNegativeValues;
    }

    @Test
    @DisplayName("Should handle DataAccessException when saving product")
    public void shouldHandleDataAccessExceptionWhenSavingProduct() {
        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setName("Product 2");
        newProduct.setDescription("Product description");
        newProduct.setPrice(50.00);
        newProduct.setCategory_Id(1);
        newProduct.setWeight(15.00);
        newProduct.setCurrent_stock(25);
        newProduct.setMin_stock(10);

        when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(ServiceException.class, () -> productServiceImpl.addProduct(newProduct));

        assertEquals("Failed to save product", exception.getMessage(), "The exception message should be 'Failed to save product'");
        assertNotNull(exception.getCause(), "The cause of the exception should not be null");
        assertEquals("Database error", exception.getCause().getMessage(), "The cause message should be 'Database error'");
    }
    @Test
    @DisplayName("Find product by ID")
    public void test_findProductById(){

        long productId = 2L;
        Product product = new Product();
        product.setId(2L);
        product.setName("Product 1");
        product.setDescription("Product description");
        product.setPrice(50.00);
        product.setCategory_Id(1);
        product.setWeight(15.00);
        product.setCurrent_stock(25);
        product.setMin_stock(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productServiceImpl.findProductById(productId);

        assertNotNull(foundProduct, "The product should not be null");
        assertEquals(productId, foundProduct.getId());
        assertEquals("Product description",foundProduct.getDescription());
        assertEquals(50.00,foundProduct.getPrice());
        assertEquals(1, foundProduct.getCategory_Id());
        assertEquals(15.00,foundProduct.getWeight());
        assertEquals(25, foundProduct.getCurrent_stock());
        assertEquals(10, foundProduct.getMin_stock());
    }
    @Test
    @DisplayName("Throw EntityNotFoundException when product not found by ID")
    public void test_findProductById_NotFound() {
        long productId = 3L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productServiceImpl.findProductById(productId));

        assertEquals("Product not found with ID: " + productId, exception.getMessage(), "The exception message should be 'Product not found with ID: " + productId);
    }


    @Test
    @DisplayName("Update product")
    public void test_updateProduct(){
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Product 1");
        existingProduct.setDescription("Product description");
        existingProduct.setPrice(50.00);
        existingProduct.setCategory_Id(1);
        existingProduct.setWeight(15.00);
        existingProduct.setCurrent_stock(25);
        existingProduct.setMin_stock(10);

        Product updatedProductDetails = new Product();
        updatedProductDetails.setId(2L);
        updatedProductDetails.setName("Product 2");
        updatedProductDetails.setDescription("Product description");
        updatedProductDetails.setPrice(60.00);
        updatedProductDetails.setCategory_Id(2);
        updatedProductDetails.setWeight(25.00);
        updatedProductDetails.setCurrent_stock(50);
        updatedProductDetails.setMin_stock(15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProductDetails);

        Product updatedProduct = productServiceImpl.updateProduct(1L, updatedProductDetails);

        assertEquals(updatedProductDetails.getName(),updatedProduct.getName());
        assertEquals(updatedProductDetails.getDescription(), updatedProduct.getDescription());
        assertEquals(updatedProductDetails.getPrice(), updatedProduct.getPrice());
        assertEquals(updatedProductDetails.getCategory_Id(), updatedProduct.getCategory_Id());
        assertEquals(updatedProductDetails.getWeight(), updatedProduct.getWeight());
        assertEquals(updatedProductDetails.getCurrent_stock(), updatedProduct.getCurrent_stock());
        assertEquals(updatedProductDetails.getMin_stock(), updatedProduct.getMin_stock());

        verify(productRepository,times(1)).findById(existingProduct.getId());
        verify(productRepository,times(1)).save(existingProduct);
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
    public void shouldThrowExceptionWhenProductDetailsAreNull() {
        long productId = 1L;
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.updateProduct(productId, null));

        assertEquals("Product details cannot be null", exception.getMessage(), "The exception message should be 'Product details cannot be null'");
    }
    @Test
    @DisplayName("Throw ServiceException when DataAccessException occurs during update")
    public void shouldThrowServiceExceptionWhenDataAccessExceptionOccurs() {
        long productId = 1L;
        Product productDetails = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(productRepository.save(any(Product.class))).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(ServiceException.class, () -> productServiceImpl.updateProduct(productId, productDetails));

        assertEquals("Failed to update the product with ID: " + productId, exception.getMessage(), "The exception message should be 'Failed to update the product with ID: " + productId);
        assertNotNull(exception.getCause(), "The cause of the exception should not be null");
        assertEquals("Database error", exception.getCause().getMessage(), "The cause message should be 'Database error'");
    }
    @Test
    @DisplayName("Delete Product - Success")
     void test_deleteProduct(){
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Product1");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productServiceImpl.deleteProduct(productId);

        verify(productRepository, times(1)).delete(product);
    }
    @Test
    @DisplayName("Delete Product - Throws RuntimeException for Non-Existent Product")
     void shouldThrowExceptionWhenDeletingNonExistentProduct() {

        long nonExistentProductId = 99L;

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.deleteProduct(nonExistentProductId));

        String expectedMessage = "Product not found with ID: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Delete Product - Handles DataAccessException")
    public void deleteProduct_DataAccessException() {
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doThrow(new DataAccessException("Database error") {}).when(productRepository).delete(product);

        Exception exception = assertThrows(DataAccessException.class, () -> productServiceImpl.deleteProduct(productId));

        assertEquals("Database error", exception.getMessage());
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Delete Product - Throws IllegalArgumentException for Invalid Product ID")
    public void deleteProduct_InvalidProductId_ThrowsIllegalArgumentException() {
        long invalidProductId = -1L;

        Exception exception = assertThrows(EntityNotFoundException.class, () -> productServiceImpl.deleteProduct(invalidProductId));

        assertEquals("Product not found with ID: "+ invalidProductId, exception.getMessage());
        verify(productRepository, never()).delete(any(Product.class));
    }
    @Test
    @DisplayName("Update product stock successfully")
    void updateProductStock_Success() {
        int newStock = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productServiceImpl.updateProductStock(productId, newStock);
        assertEquals(newStock, updatedProduct.getCurrent_stock());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Throw IllegalArgumentException for negative stock")
    void updateProductStock_NegativeStock_ThrowsIllegalArgumentException() {
        int newStock = -1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.updateProductStock(productId, newStock));

        assertEquals("Stock cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating stock")
    void updateProductStock_ProductNotFound_ThrowsRuntimeException() {
        int newStock = 20;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.updateProductStock(productId, newStock));

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product price successfully")
    void updateProductPrice_Success() {
        double newPrice = 25.0;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productServiceImpl.updateProductPrice(productId, newPrice);
        assertEquals(newPrice, updatedProduct.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Throw IllegalArgumentException for negative price")
    void updateProductPrice_NegativePrice_ThrowsIllegalArgumentException() {
        double newPrice = -5.0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productServiceImpl.updateProductPrice(productId, newPrice));

        assertEquals("Price cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating price")
    void updateProductPrice_ProductNotFound_ThrowsRuntimeException() {
        double newPrice = 30.0;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.updateProductPrice(productId, newPrice));

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Handle server error during product price update")
    void updateProductPrice_ServerError_ThrowsRuntimeException() {
        double newPrice = 25.0;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Unexpected server error"));

        Exception exception = assertThrows(RuntimeException.class, () -> productServiceImpl.updateProductPrice(productId, newPrice));

        assertEquals("Unexpected server error", exception.getMessage());
        verify(productRepository).save(product);
    }
}
