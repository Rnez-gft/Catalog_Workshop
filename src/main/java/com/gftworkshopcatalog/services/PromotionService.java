package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.PromotionEntity;

import java.util.List;

public interface PromotionService {
    List<PromotionEntity> findAllPromotions();
    PromotionEntity findPromotiontById(long promotionId);
    PromotionEntity addPromotion(PromotionEntity promotionEntity);
    PromotionEntity updatePromotion(long promotionId, PromotionEntity promotionEntityDetails);
    void deletePromotion(long promotionId);
    List<PromotionEntity> getActivePromotions();
}
