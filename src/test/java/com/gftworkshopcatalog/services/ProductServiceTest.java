package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private long productId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(productId);
        product.setName("Example Product");
        product.setCurrent_stock(10);
    }

    @Test
    @DisplayName("Update product stock successfully")
    void updateProductStock_Success() {
        int newStock = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProductStock(productId, newStock);
        assertEquals(newStock, updatedProduct.getCurrent_stock());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Throw IllegalArgumentException for negative stock")
    void updateProductStock_NegativeStock_ThrowsIllegalArgumentException() {
        int newStock = -1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductStock(productId, newStock);
        });

        assertEquals("Stock cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating stock")
    void updateProductStock_ProductNotFound_ThrowsRuntimeException() {
        int newStock = 20;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProductStock(productId, newStock);
        });

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product price successfully")
    void updateProductPrice_Success() {
        double newPrice = 25.0;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProductPrice(productId, newPrice);
        assertEquals(newPrice, updatedProduct.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Throw IllegalArgumentException for negative price")
    void updateProductPrice_NegativePrice_ThrowsIllegalArgumentException() {
        double newPrice = -5.0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductPrice(productId, newPrice);
        });

        assertEquals("Price cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Throw RuntimeException if product not found when updating price")
    void updateProductPrice_ProductNotFound_ThrowsRuntimeException() {
        double newPrice = 30.0;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProductPrice(productId, newPrice);
        });

        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Handle server error during product price update")
    void updateProductPrice_ServerError_ThrowsRuntimeException() {
        double newPrice = 25.0;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Unexpected server error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProductPrice(productId, newPrice);
        });

        assertEquals("Unexpected server error", exception.getMessage());
        verify(productRepository).save(product);
    }
}
