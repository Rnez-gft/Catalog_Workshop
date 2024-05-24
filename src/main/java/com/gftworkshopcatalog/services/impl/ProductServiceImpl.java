package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.BadRequest;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.exceptions.ServiceException;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
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
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProduct("Product not found with ID: " + productId));
    }

    public ProductEntity addProduct(ProductEntity productEntity) {
        validateProductEntity(productEntity);
        return productRepository.save(productEntity);
    }

    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        if (productEntityDetails == null) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null.");
        }

        ProductEntity productEntity = findProductById(productId);
        updateProductEntity(productEntity, productEntityDetails);
        return productRepository.save(productEntity);
    }

    private void updateProductEntity(ProductEntity existingProduct, ProductEntity newDetails) {
        existingProduct.setName(newDetails.getName());
        existingProduct.setDescription(newDetails.getDescription());
        existingProduct.setPrice(newDetails.getPrice());
        existingProduct.setCategoryId(newDetails.getCategoryId());
        existingProduct.setWeight(newDetails.getWeight());
        existingProduct.setCurrent_stock(newDetails.getCurrent_stock());
        existingProduct.setMin_stock(newDetails.getMin_stock());
    }

    public void deleteProduct(long productId) {
        ProductEntity product = findProductById(productId);
        productRepository.delete(product);
    }

    public ProductEntity updateProductPrice(long productId, double newPrice) {
        if (newPrice < 0) {
            throw new AddProductInvalidArgumentsExceptions("Price cannot be negative");
        }

        ProductEntity product = findProductById(productId);
        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    public ProductEntity updateProductStock(long productId, int quantity) {
        ProductEntity product = findProductById(productId);
        int newStock = product.getCurrent_stock() + quantity;
        if (newStock < 0) {
            throw new BadRequest("Insufficient stock to decrement by " + quantity);
        }
        product.setCurrent_stock(newStock);
        return productRepository.save(product);
    }

    public List<ProductEntity> findProductsByIds(List<Long> ids) {
        List<ProductEntity> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new NotFoundProduct("One or more product IDs not found");
        }
        return products;
    }

    public double calculateDiscountedPrice(Long id, int quantity) {
        ProductEntity product = findProductById(id);
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

    private void validateProductEntity(ProductEntity productEntity) {
        if (productEntity.getName() == null ||
                productEntity.getPrice() == null || productEntity.getPrice() < 0 ||
                productEntity.getCategoryId() == null ||
                productEntity.getWeight() == null || productEntity.getWeight() < 0 ||
                productEntity.getCurrent_stock() == null || productEntity.getCurrent_stock() < 0 ||
                productEntity.getMin_stock() == null || productEntity.getMin_stock() < 0) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not contain negative values.");
        }
    }
}