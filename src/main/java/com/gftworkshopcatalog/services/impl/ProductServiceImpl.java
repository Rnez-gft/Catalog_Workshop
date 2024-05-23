package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.exceptions.ServiceException;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.utils.ProductValidationUtils;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {



    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    public ProductServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }


    public List<ProductEntity> findAllProducts() {
            return productRepository.findAll();
    }

    public ProductEntity findProductById(long productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
            log.error("Product not found with ID: {}", productId);
            return new NotFoundProduct("Product not found with ID: " + productId);
        });
    }


    public ProductEntity addProduct(ProductEntity productEntity) {
        ProductValidationUtils.validateProductEntity(productEntity);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to save the product with ID: " + productEntity.getId(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        if (productEntityDetails == null) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null.");
        }

        ProductEntity productEntity = findProductById(productId);

        productEntity.setName(productEntityDetails.getName());
        productEntity.setDescription(productEntityDetails.getDescription());
        productEntity.setPrice(productEntityDetails.getPrice());
        productEntity.setCategoryId(productEntityDetails.getCategoryId());
        productEntity.setWeight(productEntityDetails.getWeight());
        productEntity.setCurrentStock(productEntityDetails.getCurrentStock());
        productEntity.setMinStock(productEntityDetails.getMinStock());

        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update the product with ID: " + productId, ex, HttpStatus.INTERNAL_SERVER_ERROR);
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
            throw new AddProductInvalidArgumentsExceptions("Price cannot be negative");
        }
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProduct("Product not found with ID: " + productId));
        productEntity.setPrice(newPrice);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update product price for ID: " + productId, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ProductEntity updateProductStock(long productId, int quantity) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProduct("Product not found with ID: " + productId));

        int newStock = productEntity.getCurrentStock() + quantity;
        if (newStock < 0) {
            throw new AddProductInvalidArgumentsExceptions("Insufficient stock to decrement by " + quantity);
        }

        productEntity.setCurrentStock(newStock);
        try {
            return productRepository.save(productEntity);
        } catch (DataAccessException ex) {
            throw new ServiceException("Failed to update product stock for ID: " + productId, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public List<ProductEntity> findProductsByIds(List<Long> ids) {
        List<ProductEntity> products = new ArrayList<>(productRepository.findAllById(ids));
        if (products.size() != ids.size()) {
            throw new NotFoundProduct("One or more product IDs not found");
        }
        return products;
    }


        public double calculateDiscountedPrice(Long id, int quantity) {
            ProductEntity product = productRepository.findById(id)
                    .orElseThrow(() -> new NotFoundProduct("Product not found with ID: " + id));

            PromotionEntity promotion = promotionRepository.findActivePromotionByCategoryId(product.getCategoryId());

            if (promotion != null && promotion.getIsActive() && "VOLUME".equalsIgnoreCase(promotion.getPromotionType())) {
                return calculateNewPrice(product.getPrice(), promotion, quantity);
            }

            return product.getPrice();
        }

        private double calculateNewPrice(double originalPrice, PromotionEntity promotion, int quantity) {
            if (quantity >= promotion.getVolumeThreshold()) {
                return originalPrice * (1 - promotion.getDiscount());
            }
            return originalPrice;
        }
}