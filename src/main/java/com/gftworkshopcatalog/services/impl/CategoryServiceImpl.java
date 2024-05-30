package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.*;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.CategoryRepository;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.CategoryService;
import com.gftworkshopcatalog.utils.CategoryValidationUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;


import java.util.*;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryValidationUtils categoryValidationUtils;


    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository, PromotionRepository promotionRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryValidationUtils = new CategoryValidationUtils(productRepository, promotionRepository);

    }

    public List<CategoryEntity> getAllCategories() {
            return categoryRepository.findAll();
    }

    public CategoryEntity findCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new NotFoundCategory("Category not found with ID: " + categoryId);
        });
    }


    public List<ProductEntity> findProductsByCategoryId(Long categoryId) {
        List<ProductEntity> products = productRepository.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            log.error("Category not found with ID: {}", categoryId);
            throw new NotFoundCategory("Category not found with ID: " + categoryId);
        }
        return new ArrayList<>(products);
    }


    public CategoryEntity addCategory(CategoryEntity categoryEntity) {
        if (categoryEntity.getName() == null || categoryEntity.getCategoryId() < 0) {
            throw new AddProductInvalidArgumentsExceptions("Category details must not contain null or negative values");
        }
        return categoryRepository.save(categoryEntity);
    }
    public void deleteCategoryById(long categoryId) {
        CategoryEntity categoryEntity = findCategoryById(categoryId);
        categoryValidationUtils.validateCategoryDeletion(categoryId);
        categoryRepository.delete(categoryEntity);
    }

    public List<ProductEntity> findProductsByCategoryIdAndName(Long categoryId, String name) {
        String namePrefix = formatName(name);

        List<ProductEntity> products = productRepository.findByCategoryIdAndNameStartsWith(categoryId, namePrefix);
        if (products.isEmpty()) {
            log.error("Products not founds with ID: {} and NAME: {}", categoryId, namePrefix);
            throw new NotFoundCategory("Category not found with ID: " + categoryId + " and NAME: " + namePrefix);
        }
        return new ArrayList<>(products);
    }

    public String formatName(String name) {
        String lowerCaseName = name.toLowerCase();
        return lowerCaseName.substring(0, 1).toUpperCase() + lowerCaseName.substring(1) + "%";
    }

}
