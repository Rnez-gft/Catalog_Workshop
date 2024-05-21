package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.PromotionEntity;

import java.util.List;

public interface PromotionService {
    public List<PromotionEntity> findAllPromotions();
    public PromotionEntity findPromotiontById(long promotionId);
    public PromotionEntity addProduct(PromotionEntity promotionEntity);
    public PromotionEntity updaPromotion(long promotionId, PromotionEntity promotionEntityDetails);
    public void deletePromotion(long promotionId);
}
