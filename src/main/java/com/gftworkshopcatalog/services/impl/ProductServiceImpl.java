package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.api.dto.CartProductDTO;
import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.BadRequest;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gftworkshopcatalog.operations.ProductOperations.*;
import static com.gftworkshopcatalog.utils.ProductValidationUtils.validateProductEntity;

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
        validateProductEntity(productEntity);
            return productRepository.save(productEntity);
    }


    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        if (productEntityDetails == null) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null.");
        }

        validateProductEntity(productEntityDetails);

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
        existingProduct.setCurrentStock(newDetails.getCurrentStock());
        existingProduct.setMinStock(newDetails.getMinStock());
    }

    public void deleteProduct(long productId) {
        ProductEntity productEntity = findProductById(productId);
        productRepository.delete(productEntity);
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
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProduct("Product not found with ID: " + productId));
        int newStock = product.getCurrentStock() + quantity;
        if (newStock < 0) {
            throw new BadRequest("Insufficient stock to decrement by " + quantity);
        }
        product.setCurrentStock(newStock);
        return productRepository.save(product);
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



    public List<ProductEntity> calculateListDiscountedPrice(List<CartProductDTO> cartProducts) {
        List<ProductEntity> discountedProducts = new ArrayList<>();

        for (CartProductDTO cartProduct : cartProducts) {
            ProductEntity product = findProductById(cartProduct.getProductId());
            PromotionEntity promotion = findActivePromotionByCategoryId(product.getCategoryId());
            double discountedPricePerUnit = calculateDiscountedPricePerUnit(product, promotion, cartProduct.getQuantity());

            ProductEntity discountedProduct = createDiscountedProductEntity(product, discountedPricePerUnit, cartProduct.getQuantity());
            discountedProducts.add(discountedProduct);
        }

        return discountedProducts;
    }

    private ProductEntity createDiscountedProductEntity(ProductEntity product, double discountedPricePerUnit, int quantity) {
        double totalPrice = discountedPricePerUnit * quantity;
        double totalWeight = product.getWeight() * quantity;

        ProductEntity discountedProduct = new ProductEntity();
        discountedProduct.setId(product.getId());
        discountedProduct.setName(product.getName());
        discountedProduct.setDescription(product.getDescription());
        discountedProduct.setPrice(totalPrice);
        discountedProduct.setCategoryId(product.getCategoryId());
        discountedProduct.setWeight(totalWeight);
        discountedProduct.setCurrentStock(product.getCurrentStock());
        discountedProduct.setMinStock(product.getMinStock());

        return discountedProduct;
    }

    private double calculateDiscountedPricePerUnit(ProductEntity product, PromotionEntity promotion, int quantity) {
        if (promotion != null && promotion.getIsActive() && "VOLUME".equalsIgnoreCase(promotion.getPromotionType())) {
            return calculateNewPriceV2(product.getPrice(), promotion, quantity);
        }
        return product.getPrice();
    }

    public PromotionEntity findActivePromotionByCategoryId(Long categoryId) {
        return promotionRepository.findActivePromotionByCategoryId(categoryId);
    }






}