package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
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
        if (productId < 0) {
            log.error("Invalid product ID: {}", productId);
            throw new RuntimeException("Internal server error: Invalid product ID " + productId);
        }

        try {
            return productRepository.findById(productId).orElseThrow(() -> {
                log.error("Product not found with ID: {}", productId);
                return new EntityNotFoundException("Product not found with ID: " + productId);
            });
        } catch (EntityNotFoundException e) {
            throw e; // Re-throwing the EntityNotFoundException to be handled by the controller
        } catch (Exception e) {
            log.error("Internal server error while retrieving product with ID: {}", productId, e);
            throw new RuntimeException("Internal server error while retrieving product with ID: " + productId, e);
        }
    }

    public ProductEntity addProduct(ProductEntity productEntity) {

        if (productEntity.getPrice() != null && productEntity.getPrice() < 0 ||
                productEntity.getWeight() != null && productEntity.getWeight() < 0 ||
                productEntity.getCurrent_stock() != null && productEntity.getCurrent_stock() < 0 ||
                productEntity.getMin_stock() != null && productEntity.getMin_stock() < 0) {

            throw new AddProductInvalidArgumentsExceptions("Product details must not contain negative values");
        }


        if (productEntity.getName() == null ||
                productEntity.getPrice() == null ||
                productEntity.getCategory_Id() == null ||
                productEntity.getWeight() == null ||
                productEntity.getCurrent_stock() == null ||
                productEntity.getMin_stock() == null) {
            throw new IllegalArgumentException("Product details must not be null except description");
        }

        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to save product", ex);
            throw new RuntimeException("Failed to save product", ex);
        }
    }


    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        if (productEntityDetails == null) {
            throw new IllegalArgumentException("Product details must not be null.");
        }

        try {
            ProductEntity productEntity = findProductById(productId);
            if (productEntity == null) {
                throw new EntityNotFoundException("Product not found with ID: " + productId);
            }

            productEntity.setName(productEntityDetails.getName());
            productEntity.setDescription(productEntityDetails.getDescription());
            productEntity.setPrice(productEntityDetails.getPrice());
            productEntity.setCategory_Id(productEntityDetails.getCategory_Id());
            productEntity.setWeight(productEntityDetails.getWeight());
            productEntity.setCurrent_stock(productEntityDetails.getCurrent_stock());
            productEntity.setMin_stock(productEntityDetails.getMin_stock());

            return productRepository.save(productEntity);
        } catch (IllegalArgumentException ex) {
            log.error("Illegal argument while updating the product with ID: {}", productId, ex);
            throw ex;
        } catch (EntityNotFoundException ex) {
            log.error("Product not found while updating the product with ID: {}", productId, ex);
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Failed to update the product with ID: {}", productId, ex);
            throw new ServiceException("Failed to update the product with ID: " + productId, ex);
        } catch (Exception ex) {
            log.error("Unexpected error while updating the product with ID: {}", productId, ex);
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


  public ProductEntity updateProductStock(long productId, int quantity) {

        ProductEntity productEntity = findProductById(productId);
        validateProductEntity(productEntity, productId);

        int newStock = productEntity.getCurrent_stock() + quantity;

        if (newStock <= productEntity.getMin_stock()) {
            log.info("Min stock reached. Product stock must be updated with ID: {}", productId);
        } else if(newStock < 0){

            log.info("Error updating stock for product with ID: {}, insufficient current stock", productId);
            throw new IllegalArgumentException("Insufficient stock to decrement by " + quantity);
        }

        productEntity.setCurrent_stock(newStock);
        log.info("Updating stock for product with ID: {}", productId);

        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to update product stock for ID: {}", productId, ex);
            throw new RuntimeException("Failed to update product stock for ID: " + productId, ex);
        }
    }

    private void validateProductEntity(ProductEntity productEntity, long productId) {
        if (productEntity == null) {
            log.error("Product not found with ID: {}", productId);
            throw new IllegalArgumentException("ProductEntity cannot be null.");
        }

        if (productEntity.getPrice() < 0 || productEntity.getWeight() < 0 ||
                productEntity.getCurrent_stock() < 0 || productEntity.getMin_stock() < 0) {
            throw new IllegalArgumentException("Product details must not contain negative values.");
        }
    }
}