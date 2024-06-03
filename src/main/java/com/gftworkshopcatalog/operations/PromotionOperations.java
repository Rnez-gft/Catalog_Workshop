package com.gftworkshopcatalog.operations;

import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.PromotionRepository;

public class PromotionOperations {
    static PromotionRepository promotionRepository;

    public static PromotionEntity findActivePromotionByCategoryId(Long categoryId) {
        return promotionRepository.findActivePromotionByCategoryId(categoryId);
    }
}
