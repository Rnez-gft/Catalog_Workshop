package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.exceptions.*;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Find all products - Success")
    void findAllProducts_Success() {
        List<ProductEntity> products = List.of(new ProductEntity());
        when(productRepository.findAll()).thenReturn(products);
        List<ProductEntity> result = productServiceImpl.findAllProducts();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Find product by ID - Success")
    void findProductById_Success() {
        long productId = 1L;
        ProductEntity product = new ProductEntity();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        ProductEntity result = productServiceImpl.findProductById(productId);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Find product by ID - Not Found")
    void findProductById_NotFound() {
        long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(NotFoundProduct.class, () -> productServiceImpl.findProductById(productId));
    }

    @Test
    @DisplayName("Add product - Success")
    void addProduct_Success() {
        ProductEntity product = new ProductEntity();
        product.setName("Sample Product");
        product.setPrice(100.0);
        product.setCategoryId(1L);
        product.setWeight(10.0);
        product.setCurrentStock(50);
        product.setMinStock(5);

        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        ProductEntity result = productServiceImpl.addProduct(product);

        assertNotNull(result, "The saved product should not be null");
        assertEquals(100.0, result.getPrice(), "The price should match the input");
    }

    @Test
    @DisplayName("Update product - Success")
    void updateProduct_Success() {
        long productId = 1L;
        ProductEntity existingProduct = new ProductEntity();
        ProductEntity productDetails = new ProductEntity();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(existingProduct);
        ProductEntity result = productServiceImpl.updateProduct(productId, productDetails);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Delete product - Success")
    void deleteProduct_Success() {
        long productId = 1L;
        ProductEntity product = new ProductEntity();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        productServiceImpl.deleteProduct(productId);
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Update product price - Success")
    void updateProductPrice_Success() {
        long productId = 1L;
        double newPrice = 150.0;
        ProductEntity product = new ProductEntity();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        ProductEntity result = productServiceImpl.updateProductPrice(productId, newPrice);
        assertEquals(newPrice, result.getPrice());
    }

    @Test
    @DisplayName("Update product stock - Success")
    void updateProductStock_Success() {
        long productId = 1L;
        int quantity = 5;
        ProductEntity product = new ProductEntity();
        product.setCurrentStock(10);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        ProductEntity result = productServiceImpl.updateProductStock(productId, quantity);
        assertEquals(15, result.getCurrentStock());
    }

    @Test
    @DisplayName("Find products by IDs - Success")
    void findProductsByIds_Success() {
        List<Long> ids = List.of(1L, 2L);
        List<ProductEntity> products = List.of(new ProductEntity(), new ProductEntity());
        when(productRepository.findAllById(ids)).thenReturn(products);
        List<ProductEntity> result = productServiceImpl.findProductsByIds(ids);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Calculate discounted price - No active promotion")
    void calculateDiscountedPrice_NoActivePromotion() {
        long productId = 1L;
        int quantity = 1;
        ProductEntity product = new ProductEntity();
        product.setPrice(100.0);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(promotionRepository.findActivePromotionByCategoryId(product.getCategoryId())).thenReturn(null);
        double result = productServiceImpl.calculateDiscountedPrice(productId, quantity);
        assertEquals(100.0, result);
    }

}
