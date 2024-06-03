package com.gftworkshopcatalog.operations;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;

public class ProductOperations {
    public static double calculateNewPrice(double originalPrice, PromotionEntity promotion, int quantity) {
        if (quantity >= promotion.getVolumeThreshold()) {
            return originalPrice * (1 - promotion.getDiscount());
        }
        return originalPrice;
    }

    public static double calculateDiscountedPricePerUnit(ProductEntity product, PromotionEntity promotion, int quantity) {
        if (promotion != null && promotion.getIsActive() && "VOLUME".equalsIgnoreCase(promotion.getPromotionType())) {
            return calculateNewPriceV2(product.getPrice(), promotion, quantity);
        }
        return product.getPrice();
    }

    public static double calculateNewPriceV2(double originalPrice, PromotionEntity promotion, int quantity) {
        if (quantity >= promotion.getVolumeThreshold()) {
            return originalPrice * (1 - promotion.getDiscount());
        }
        return originalPrice;
    }

    public static ProductEntity createDiscountedProductEntity(ProductEntity product, double discountedPricePerUnit, int quantity) {
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
}
