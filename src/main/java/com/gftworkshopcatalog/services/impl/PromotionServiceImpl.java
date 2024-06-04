package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.NotFoundPromotion;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.PromotionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import static com.gftworkshopcatalog.utils.PromotionValidationUtils.validatePromotionEntity;


@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private static final String PROMOTION_NOT_FOUND = "Promotion not found with ID: ";
    private static final String PROMOTION_DETAILS_NULL = "Promotion details must not be null";
    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        StatusPromotionServiceImpl statusPromotionService = new StatusPromotionServiceImpl(productRepository ,promotionRepository);
        statusPromotionService.getActivePromotions();
    }

    public List<PromotionEntity> findAllPromotions() {
        log.info("Retrieving all promotions.");
            return promotionRepository.findAll();
    }
    public PromotionEntity findPromotionById(long promotionId) {
        log.info("Searching for promotion by ID: {}", promotionId);
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundPromotion(PROMOTION_NOT_FOUND + promotionId));
    }
    public PromotionEntity addPromotion(PromotionEntity promotionEntity) {
        if (promotionEntity == null) {
            log.error(PROMOTION_DETAILS_NULL);
            throw new IllegalArgumentException(PROMOTION_DETAILS_NULL);
        }
        validatePromotionEntity(promotionEntity);
        return promotionRepository.save(promotionEntity);
    }
    public PromotionEntity updatePromotion(long promotionId, PromotionEntity promotionEntityDetails) {
        if (promotionEntityDetails == null) {
            log.error("Details null in the new promotion");
            throw new AddProductInvalidArgumentsExceptions(PROMOTION_DETAILS_NULL);
        }
        validatePromotionEntity(promotionEntityDetails);
        PromotionEntity existingPromotion = findPromotionById(promotionId);
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
        PromotionEntity promotion = findPromotionById(promotionId);
        log.info("Deleting promotion with ID: {}", promotionId);
        promotionRepository.delete(promotion);
    }
}

