package com.gftworkshopcatalog.utils;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;

import com.gftworkshopcatalog.model.PromotionEntity;

public class PromotionValidationUtils {
    private PromotionValidationUtils() {
    }

    public static void validatePromotionEntity(PromotionEntity promotionEntity) {
        if (promotionEntity.getCategoryId() == null || promotionEntity.getCategoryId() < 0 ||
                promotionEntity.getDiscount() == null || promotionEntity.getDiscount() < 0 ||
                promotionEntity.getPromotionType() == null ||
                promotionEntity.getVolumeThreshold() == null || promotionEntity.getVolumeThreshold() < 0 ||
                promotionEntity.getStartDate() == null ||
                promotionEntity.getEndDate() == null) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null except description");
        }
    }

}