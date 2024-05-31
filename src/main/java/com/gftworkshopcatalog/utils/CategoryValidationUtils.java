package com.gftworkshopcatalog.utils;


import com.gftworkshopcatalog.exceptions.InternalServiceException;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;

import java.util.List;

public class CategoryValidationUtils {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

   public CategoryValidationUtils(ProductRepository productRepository,
                                  PromotionRepository promotionRepository) {
       this.productRepository = productRepository;
       this.promotionRepository = promotionRepository;
   }
    public void validateCategoryDeletion(Long categoryId){
        List<ProductEntity> products = productRepository.findByCategoryId(categoryId);
        if (!products.isEmpty()){
            throw new InternalServiceException("Cannot delete category because it´s referenced by products.");
        }
        List<PromotionEntity> promotions = promotionRepository.findByCategoryId(categoryId);
        if (!promotions.isEmpty()){
            throw new InternalServiceException("Cannot delete category because it´s reference by promotions.");
        }
    }
}
