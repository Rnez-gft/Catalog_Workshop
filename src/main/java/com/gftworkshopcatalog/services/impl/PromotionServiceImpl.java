package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.api.dto.PromotionDTO;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
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
            return new EntityNotFoundException("Promotion not found with ID: " + promotionId);
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
        PromotionEntity existingPromotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with ID: " + promotionId));
        
        updatePromotionEntity(existingPromotion, promotionEntityDetails);
        
        try{
            return promotionRepository.save(existingPromotion);
        } catch (DataException ex) {
            throw new RuntimeException("Failed to update the promotion with ID: " + promotionId, ex);
        }
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
        PromotionEntity promotionEntity = findPromotiontById(promotionId);
        log.info("Deleting promotion with ID: {}", promotionId);
        try {
            promotionRepository.delete(promotionEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to delete promotion with ID: {}", promotionId, ex);
            throw new EntityNotFoundException("Failed to delete promotion with ID: " + promotionId, ex);
        }
    }
}
