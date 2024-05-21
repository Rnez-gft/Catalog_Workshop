package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.api.dto.PromotionDTO;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
        return null;
    }

    public PromotionEntity updaPromotion(long promotionId, PromotionEntity promotionEntityDetails) {
        return null;
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
