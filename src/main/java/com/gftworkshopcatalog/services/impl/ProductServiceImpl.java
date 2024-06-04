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
    private static final String PRODUCT_NOT_FOUND_LOG = "Product not found with ID: {}";
    private static final String PRODUCT_NOT_FOUND_ERROR = "\"Product not found with ID: \"";

    public ProductServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }


    public List<ProductEntity> findAllProducts() {
        log.info("Retrieving all products");
        return productRepository.findAll();
    }

    public ProductEntity findProductById(long productId) {
        log.info("Retrieving product by its ID");
        return productRepository.findById(productId).orElseThrow(() -> {
            log.error(PRODUCT_NOT_FOUND_LOG, productId);
            return new NotFoundProduct(PRODUCT_NOT_FOUND_ERROR + productId);
        });
    }


    public ProductEntity addProduct(ProductEntity productEntity) {
        log.info("Adding new product: {}", productEntity);
        validateProductEntity(productEntity);
        return productRepository.save(productEntity);
    }


    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails) {
        log.info("Updating product ID: {}", productId);
        if (productEntityDetails == null) {
            log.error("Failed to update product: Product details must not be null");
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null.");
        }

        validateProductEntity(productEntityDetails);

        ProductEntity productEntity = findProductById(productId);
        updateProductEntity(productEntity, productEntityDetails);
        return productRepository.save(productEntity);
    }

    private void updateProductEntity(ProductEntity existingProduct, ProductEntity newDetails) {
        log.info("Updating existing product details for product ID: {}", existingProduct.getId());
        existingProduct.setName(newDetails.getName());
        existingProduct.setDescription(newDetails.getDescription());
        existingProduct.setPrice(newDetails.getPrice());
        existingProduct.setCategoryId(newDetails.getCategoryId());
        existingProduct.setWeight(newDetails.getWeight());
        existingProduct.setCurrentStock(newDetails.getCurrentStock());
        existingProduct.setMinStock(newDetails.getMinStock());
    }

    public void deleteProduct(long productId) {
        log.info("Deleting product ID: {}", productId);
        ProductEntity productEntity = findProductById(productId);
        productRepository.delete(productEntity);
        log.info("Deleted product ID: {}", productId);
    }

    public ProductEntity updateProductPrice(long productId, double newPrice) {
        log.info("Updating price for product ID: {}", productId);
        if (newPrice < 0) {
            log.info("Failed to update price: Price cannot be negative");
            throw new AddProductInvalidArgumentsExceptions("Price cannot be negative");
        }

        ProductEntity product = findProductById(productId);
        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    public ProductEntity updateProductStock(long productId, int quantity) {
        log.info("Updating stock for product ID: {}", productId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundProduct(PRODUCT_NOT_FOUND_ERROR + productId));
        int newStock = product.getCurrentStock() + quantity;
        if (newStock < 0) {
            log.error("Insufficient stock to decrement for product ID: {}", productId);
            throw new BadRequest("Insufficient stock to decrement by " + quantity);
        }
        product.setCurrentStock(newStock);
        return productRepository.save(product);
    }


    public List<ProductEntity> findProductsByIds(List<Long> ids) {
        log.info("Finding products by IDs");
        List<ProductEntity> products = new ArrayList<>(productRepository.findAllById(ids));
        if (products.size() != ids.size()) {
            log.warn("Mismatch in found products by IDs");
            throw new NotFoundProduct("One or more product IDs not found");
        }
        return products;
    }


    public double calculateDiscountedPrice(Long id, int quantity) {
        log.info("Calculating discounted price for product ID: {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundProduct(PRODUCT_NOT_FOUND_ERROR + id));

        PromotionEntity promotion = promotionRepository.findActivePromotionByCategoryId(product.getCategoryId());

        if (promotion != null && promotion.getIsActive() && "VOLUME".equalsIgnoreCase(promotion.getPromotionType())) {
            return calculateNewPrice(product.getPrice(), promotion, quantity);
        }

        return product.getPrice();
    }


    public List<ProductEntity> calculateListDiscountedPrice(List<CartProductDTO> cartProducts) {
        log.info("Calculating discounted prices for list of cart products");
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
        log.debug("Creating discounted product entity for product ID: {}", product.getId());
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
        log.debug("Calculating discounted price per unit for product ID: {}", product.getId());
        if (promotion != null && promotion.getIsActive() && "VOLUME".equalsIgnoreCase(promotion.getPromotionType())) {
            return calculateNewPrice(product.getPrice(), promotion, quantity);
        }
        return product.getPrice();
    }

    public PromotionEntity findActivePromotionByCategoryId(Long categoryId) {
        log.info("Finding active promotion by category ID: {}", categoryId);
        return promotionRepository.findActivePromotionByCategoryId(categoryId);
    }


}