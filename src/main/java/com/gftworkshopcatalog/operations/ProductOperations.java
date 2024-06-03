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



    public static double calculateNewPriceV2(double originalPrice, PromotionEntity promotion, int quantity) {
        if (quantity >= promotion.getVolumeThreshold()) {
            return originalPrice * (1 - promotion.getDiscount());
        }
        return originalPrice;
    }


}
