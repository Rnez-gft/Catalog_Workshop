package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void test_findAllProducts(){
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

        List<Product> allProducts = productService.findAllProducts();

        assertNotNull(allProducts, "The product list should not be null");
        assertEquals(2, allProducts.size(),"The product list should contain 2 items");
        assertTrue(allProducts.contains(product1), "The list should contain 'Product 1'");
        assertTrue(allProducts.contains(product2), "The list should contain 'Product 2'");
    }
    @Test
    public void shouldReturnEmptyListWhenNoProductsExists(){

        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> allProducts = productService.findAllProducts();

        assertNotNull(allProducts,"The product list shoul not be null");
        assertTrue(allProducts.isEmpty(),"The product list should be empty");
    }
    @Test
    public void test_AddProduct (){
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

        Product result = productService.addProduct(newProduct);

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
    public void test_finProductById(){
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

        Product foundProduct = productService.findProductById(productId);

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

        Product updatedProduct = productService.updateProduct(1L, updatedProductDetails);

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
    public void shouldThrowExceptionWhenProductNotFound(){
        long nonExistentProductId = 99L;
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.findProductById(nonExistentProductId);
        });

        String expectedMessage = "Product not found with id: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
    @Test
    public void test_deleteProduct(){
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Computadora");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).delete(product);
    }
    @Test
    public void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        long nonExistentProductId = 99L;

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(nonExistentProductId);
        });

        String expectedMessage = "Product not found with id: " + nonExistentProductId;
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(productRepository, never()).delete(any(Product.class));
    }
}
