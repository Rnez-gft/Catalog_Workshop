package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.NotFoundPromotion;
import com.gftworkshopcatalog.model.ProductEntity;

import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.PromotionService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        getActivePromotions();
    }

    public List<PromotionEntity> findAllPromotions() {
        try {
            return promotionRepository.findAll();
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
    }

    public PromotionEntity findPromotiontById(long promotionId) {
        return promotionRepository.findById(promotionId).orElseThrow(() -> {
            log.error("Promotion not found with ID: {}", promotionId);
            return new NotFoundPromotion("Promotion not found with ID: " + promotionId);
        });
    }

    public PromotionEntity addPromotion(PromotionEntity promotionEntity) {
        if (promotionEntity == null) {
            throw new IllegalArgumentException("Promotion details must not be null");
        }

        try {
            return promotionRepository.save(promotionEntity);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to add the promotion due to database error", ex);
        }
    }
    public PromotionEntity updatePromotion(long promotionId, PromotionEntity promotionEntityDetails) {
        PromotionEntity existingPromotion = findPromotiontById(promotionId);
        updatePromotionEntity(existingPromotion, promotionEntityDetails);
        return promotionRepository.save(existingPromotion);
    }
    private void updatePromotionEntity(PromotionEntity existingPromotion, PromotionEntity newDetails) {
        existingPromotion.setCategoryId(newDetails.getCategoryId());
        existingPromotion.setDiscount(newDetails.getDiscount());
        existingPromotion.setPromotionType(newDetails.getPromotionType());
        existingPromotion.setVolumeThreshold(newDetails.getVolumeThreshold());
        existingPromotion.setStartDate(newDetails.getStartDate());
        existingPromotion.setEndDate(newDetails.getEndDate());
    }

    public void deletePromotion(long promotionId) {
        PromotionEntity promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundPromotion("Promotion not found with ID: " + promotionId));
        promotionRepository.delete(promotion);
    }

    public List<PromotionEntity> getActivePromotions() {
        try {
            List<PromotionEntity> promotions = promotionRepository.findAll();
            promotions.forEach(this::updateIsActiveStatus);
            applyActivePromotions(promotions.stream()
                    .filter(promotion -> promotion.getIsActive() && promotion.getPromotionType().equals("SEASONAL"))
                    .collect(Collectors.toList()));
            return promotions.stream()
                    .filter(PromotionEntity::getIsActive)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
    }

    private void updateIsActiveStatus(PromotionEntity promotion) {
        LocalDate now = LocalDate.now();
        boolean isActive = (now.isEqual(promotion.getStartDate()) || now.isAfter(promotion.getStartDate())) &&
                (now.isEqual(promotion.getEndDate()) || now.isBefore(promotion.getEndDate()));
        promotion.setIsActive(isActive);
    }

    private void applyActivePromotions(List<PromotionEntity> activePromotions) {
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
