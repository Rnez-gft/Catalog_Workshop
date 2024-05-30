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

import static com.gftworkshopcatalog.utils.ProductValidationUtils.validateProductEntity;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {



    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    String notFound="Product not found with ID: ";

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
            return new NotFoundProduct(notFound + productId);
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
                .orElseThrow(() -> new NotFoundProduct(notFound + productId));
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

        public double calculateNewPrice(double originalPrice, PromotionEntity promotion, int quantity) {
            if (quantity >= promotion.getVolumeThreshold()) {
                return originalPrice * (1 - promotion.getDiscount());
            }
            return originalPrice;
        }

    public List<ProductEntity> calculateCartProductPrice(List<CartProductDTO> cartProducts) {
        List<ProductEntity> discountedProducts = new ArrayList<>();

        for (CartProductDTO cartProduct : cartProducts) {
            Long productId = cartProduct.getProductId();
            int quantity = cartProduct.getQuantity();

            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundProduct(notFound + productId));

            PromotionEntity promotion = promotionRepository.findActivePromotionByCategoryId(product.getCategoryId());

            double discountedPricePerUnit = product.getPrice();
            if (promotion != null && promotion.getIsActive() && "VOLUME".equalsIgnoreCase(promotion.getPromotionType())) {
                discountedPricePerUnit = calculateNewPrice(product.getPrice(), promotion, quantity);
            }


            double totalPrice = discountedPricePerUnit * quantity;
            double totalWeight = product.getWeight() * quantity;

            ProductEntity discountedProduct = ProductEntity.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(totalPrice)
                    .categoryId(product.getCategoryId())
                    .weight(totalWeight)
                    .currentStock(product.getCurrentStock())
                    .minStock(product.getMinStock())
                    .build();


            discountedProducts.add(discountedProduct);
        }

        return discountedProducts;
    }

}