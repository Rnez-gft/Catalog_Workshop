package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.PromotionEntity;

import java.util.List;

public interface StatusPromotionService {
    public List<PromotionEntity> getActivePromotions();
    public void updateIsActiveStatus(PromotionEntity promotion);
    public void applyActivePromotions(List<PromotionEntity> activePromotions);
}
