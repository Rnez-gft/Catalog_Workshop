package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductEntity> findAllProducts() {
        try {
            return productRepository.findAll();
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
    }

    public ProductEntity findProductById(long productId) {
        return productRepository.findById(productId).orElseGet(() -> {
            log.error("Product not found with ID: {}", productId);
            return null;
        });
    }

    public ProductEntity addProduct(ProductEntity productEntity) {
        validateProductEntity(productEntity);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to save product", ex);
            throw new RuntimeException("Failed to save product", ex);
        }
    }

    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        ProductEntity productEntity = findProductById(productId);
        validateProductEntity(productEntityDetails);
        log.info("Updating product with ID: {}", productId);

        productEntity.setName(productEntityDetails.getName());
        productEntity.setDescription(productEntityDetails.getDescription());
        productEntity.setPrice(productEntityDetails.getPrice());
        productEntity.setCategory_Id(productEntityDetails.getCategory_Id());
        productEntity.setWeight(productEntityDetails.getWeight());
        productEntity.setCurrent_stock(productEntityDetails.getCurrent_stock());
        productEntity.setMin_stock(productEntityDetails.getMin_stock());

        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to update the product with ID: {}", productId, ex);
            throw new RuntimeException("Failed to update the product with ID: " + productId, ex);
        }
    }

    public void deleteProduct(long productId) {
        ProductEntity productEntity = findProductById(productId);
        log.info("Deleting product with ID: {}", productId);
        try {
            productRepository.delete(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to delete product with ID: {}", productId, ex);
            throw new RuntimeException("Failed to delete product with ID: " + productId, ex);
        }
    }

    public ProductEntity updateProductPrice(long productId, double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        ProductEntity productEntity = findProductById(productId);
        productEntity.setPrice(newPrice);
        log.info("Updating price for product with ID: {}", productId);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to update product price for ID: {}", productId, ex);
            throw new RuntimeException("Failed to update product price for ID: " + productId, ex);
        }
    }

    public ProductEntity updateProductStock(long productId, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        ProductEntity productEntity = findProductById(productId);
        productEntity.setCurrent_stock(newStock);
        log.info("Updating stock for product with ID: {}", productId);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to update product stock for ID: {}", productId, ex);
            throw new RuntimeException("Failed to update product stock for ID: " + productId, ex);
        }
    }

    private void validateProductEntity(ProductEntity productEntity) {
        if (productEntity == null) {
            throw new IllegalArgumentException("ProductEntity cannot be null.");
        }

        if (productEntity.getName() == null || productEntity.getPrice() == null ||
                productEntity.getCategory_Id() == null || productEntity.getWeight() == null ||
                productEntity.getCurrent_stock() == null || productEntity.getMin_stock() == null) {
            throw new IllegalArgumentException("Product details must not be null except description.");
        }

        if (productEntity.getPrice() < 0 || productEntity.getWeight() < 0 ||
                productEntity.getCurrent_stock() < 0 || productEntity.getMin_stock() < 0) {
            throw new IllegalArgumentException("Product details must not contain negative values.");
        }
    }
}