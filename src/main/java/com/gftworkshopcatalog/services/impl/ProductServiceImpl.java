package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductEntity> findAllProducts() {
        try {
            return productRepository.findAll();
        } catch (DataAccessException ex) {
            throw new ServiceException("Error accessing data from database", ex);
        }
    }

    public ProductEntity findProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }

    public ProductEntity addProduct(ProductEntity productEntity) {
        if (productEntity == null || productEntity.getName() == null || productEntity.getPrice() == null || productEntity.getCategory_Id() == null || productEntity.getWeight() == null
                || productEntity.getCurrent_stock() == null || productEntity.getMin_stock() == null) {
            throw new IllegalArgumentException("Product details must not be null except description");
        }
        if (productEntity.getPrice() < 0 || productEntity.getWeight() < 0 || productEntity.getCurrent_stock() < 0 || productEntity.getMin_stock() < 0) {
            throw new IllegalArgumentException("Product details must not contain negative values");
        }
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to save product", ex);
        }
    }




    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        if (productEntityDetails == null) {
            throw new IllegalArgumentException("Product details cannot be null");
        }

        try {
            productEntity.setName(productEntityDetails.getName());
            productEntity.setDescription(productEntityDetails.getDescription());
            productEntity.setPrice(productEntityDetails.getPrice());
            productEntity.setCategory_Id(productEntityDetails.getCategory_Id());
            productEntity.setWeight(productEntityDetails.getWeight());
            productEntity.setCurrent_stock(productEntityDetails.getCurrent_stock());
            productEntity.setMin_stock(productEntityDetails.getMin_stock());

            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update the product with ID: " + productId, ex);
        }
    }

    public void deleteProduct(long productId) {
        ProductEntity productEntity = findProductById(productId); // Uses EntityNotFoundException if not found
        try {
            productRepository.delete(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to delete product with ID: " + productId, ex);
        }
    }

    public ProductEntity updateProductPrice(long productId, double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        ProductEntity productEntity = findProductById(productId); // Uses EntityNotFoundException if not found

        productEntity.setPrice(newPrice);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update product price for ID: " + productId, ex);
        }
    }

    public ProductEntity updateProductStock(long productId, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        ProductEntity productEntity = findProductById(productId); // Uses EntityNotFoundException if not found
        productEntity.setCurrent_stock(newStock);

        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update product stock for ID: " + productId, ex);
        }
    }
}
