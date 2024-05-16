package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Service
public class ProductServiceImpl {
    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAllProducts() {
        try {
            return productRepository.findAll();
        } catch (DataAccessException ex) {
            throw new ServiceException("Error accessing data from database", ex);
        }
    }

    public Product findProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }

    public Product addProduct(Product product) {
        if (product == null || product.getName() == null || product.getPrice() == null || product.getCategory_Id() == null || product.getWeight() == null
                || product.getCurrent_stock() == null || product.getMin_stock() == null) {
            throw new IllegalArgumentException("Product details must not be null except description");
        }
        if (product.getPrice() < 0 || product.getWeight() < 0 || product.getCurrent_stock() < 0 || product.getMin_stock() < 0) {
            throw new IllegalArgumentException("Product details must not contain negative values");
        }
        try {
            return productRepository.save(product);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to save product", ex);
        }
    }




    public Product updateProduct(Long productId, Product productDetails) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        if (productDetails == null) {
            throw new IllegalArgumentException("Product details cannot be null");
        }

        try {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setCategory_Id(productDetails.getCategory_Id());
            product.setWeight(productDetails.getWeight());
            product.setCurrent_stock(productDetails.getCurrent_stock());
            product.setMin_stock(productDetails.getMin_stock());

            return productRepository.save(product);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update the product with ID: " + productId, ex);
        }
    }



    public void deleteProduct(long productId) {
        Product product = findProductById(productId);
        try {
            productRepository.delete(product);
        } catch (DataAccessException ex) {
            throw new DataIntegrityViolationException("Database error", ex);
        } catch (ServiceException ex) {
            throw new ServiceException("Failed to delete product with ID: " + productId, ex);
        }
    }


    public Product updateProductPrice(long productId, double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        Product product = findProductById(productId); // Uses EntityNotFoundException if not found

        product.setPrice(newPrice);
        try {
            return productRepository.save(product);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update product price for ID: " + productId, ex);
        }
    }

    public Product updateProductStock(long productId, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        Product product = findProductById(productId); // Uses EntityNotFoundException if not found
        product.setCurrent_stock(newStock);

        try {
            return productRepository.save(product);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update product stock for ID: " + productId, ex);
        }
    }
}
