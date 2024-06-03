package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.StatusPromotionService;

import java.time.LocalDate;
import java.util.List;

public class StatusPromotionServiceImpl implements StatusPromotionService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    public StatusPromotionServiceImpl(ProductRepository productRepository, PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
    }

    public List<PromotionEntity> getActivePromotions() {
        List<PromotionEntity> promotions = promotionRepository.findAll();
        promotions.forEach(this::updateIsActiveStatus);
        applyActivePromotions(promotions.stream()
                .filter(promotion -> promotion.getIsActive() && promotion.getPromotionType().equals("SEASONAL"))
                .toList());
        return promotions.stream()
                .filter(PromotionEntity::getIsActive)
                .toList();
    }

    public void updateIsActiveStatus(PromotionEntity promotion) {
        LocalDate now = LocalDate.now();
        boolean isActive = (now.isEqual(promotion.getStartDate()) || now.isAfter(promotion.getStartDate())) &&
                (now.isEqual(promotion.getEndDate()) || now.isBefore(promotion.getEndDate()));
        promotion.setIsActive(isActive);
    }

    public void applyActivePromotions(List<PromotionEntity> activePromotions) {
        activePromotions.forEach(promotion -> {
            List<ProductEntity> products = productRepository.findByCategoryId(promotion.getCategoryId());
            products.forEach(product -> {
                double newPrice = product.getPrice() * (1 - promotion.getDiscount());
                product.setPrice(newPrice);
                productRepository.save(product);
            });
        });
    }
}
