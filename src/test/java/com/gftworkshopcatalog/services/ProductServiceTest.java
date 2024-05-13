package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.api.dto.ProductDto;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_findAllProducts(){
//        ProductDto product1 = ProductDto.builder()
//                .id(1L)
//                .name("Product 1")
//                .price(50.0)
//                .build();

        Product product1= new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(50.0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(100.0);

        List<Product> mockProducts = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(mockProducts);

        List<Product> allProducts = productService.findAllProducts();

        assertNotNull(allProducts, "The product list should not be null");
        assertEquals(2, allProducts.size(), "The product list should contain 2 items");
        assertTrue(allProducts.contains(product1), "The list should contain 'Product 1'");
        assertTrue(allProducts.contains(product2), "The list should contain 'Product 2'");
    }
    @Test
    public void shouldReturnEmptyListWhenNoProductsExist() {

        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> allProducts = productService.findAllProducts();

        assertNotNull(allProducts, "The product list should not be null");
        assertTrue(allProducts.isEmpty(), "The product list should be empty");
    }
    @Test
    public void test_addProduct(){
        Product newProduct = new Product();
        newProduct.setName("Computer");
        newProduct.setDescription("A brand new computer");
        newProduct.setPrice(99.99);
        newProduct.setStock(100L);
        newProduct.setCategory("Electronics");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Computer");
        savedProduct.setDescription("A brand new computer");
        savedProduct.setPrice(99.99);
        savedProduct.setStock(100L);
        savedProduct.setCategory("Electronics");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.addProduct(newProduct);

        assertNotNull(result, "The saved product should not be null");
        assertEquals(1L, result.getId(), "The product ID should be 1");
        assertEquals("Computer", result.getName(), "The product name should be 'Computer'");
        assertEquals(99.99, result.getPrice(), 0.01, "The price should match the saved value");
    }
    @Test
    public void test_findProductById(){
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Computadora");
        product.setPrice(100.0);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productService.findProductById(productId);

        assertNotNull(foundProduct,"The product should not be null");
        assertEquals(productId, foundProduct.getId());
        assertEquals("Computadora", foundProduct.getName());
        assertEquals(100.0, foundProduct.getPrice());
    }

    @Test
    public void updateProduct(){
        Product existingProduct = new Product(1L, "Bicicleta", "High-quality mountain bike", 299.99, 15L, "Sports", 0.10,1.0);
        Product updatedProductDetails = new Product(1L, "Updated Bike", "Updated description", 399.99, 20L, "Sports", 0.15,1.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProductDetails);

        Product updatedProduct = productService.updateProduct(1L, updatedProductDetails);

        assertEquals(updatedProductDetails.getName(), updatedProduct.getName());
        assertEquals(updatedProductDetails.getDescription(), updatedProduct.getDescription());
        assertEquals(updatedProductDetails.getPrice(), updatedProduct.getPrice());
        assertEquals(updatedProductDetails.getStock(), updatedProduct.getStock());
        assertEquals(updatedProductDetails.getCategory(), updatedProduct.getCategory());
        assertEquals(updatedProductDetails.getDiscount(), updatedProduct.getDiscount());
        assertEquals(updatedProductDetails.getWeight(), updatedProduct.getWeight());

        verify(productRepository, times(1)).findById(existingProduct.getId());
        verify(productRepository, times(1)).save(existingProduct);
    }
    @Test
    public void shouldThrowExceptionWhenProductNotFound() {
        Long nonExistentProductId = 99L;

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.findProductById(nonExistentProductId);
        });

        String expectedMessage = "Product not found with id: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
    @Test
    public void deleteProduct() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Computadora");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).delete(product);
    }
    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        Long nonExistentProductId = 99L;

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(nonExistentProductId);
        });

        String expectedMessage = "Product not found with id: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(productRepository, never()).delete(any(Product.class));
    }
}
 