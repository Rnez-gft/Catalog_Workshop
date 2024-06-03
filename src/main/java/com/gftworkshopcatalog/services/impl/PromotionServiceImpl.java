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
            return promotionRepository.findAll();
    }
    public PromotionEntity findPromotionById(long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundPromotion("Promotion not found with ID: " + promotionId));
    }
    public PromotionEntity addPromotion(PromotionEntity promotionEntity) {
        if (promotionEntity == null) {
            throw new IllegalArgumentException("Promotion details must not be null");
        }
        validatePromotionEntity(promotionEntity);

        return promotionRepository.save(promotionEntity);
    }
    public PromotionEntity updatePromotion(long promotionId, PromotionEntity promotionEntityDetails) {

        if (promotionEntityDetails == null) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null.");
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
        promotionRepository.delete(promotion);
    }
}

