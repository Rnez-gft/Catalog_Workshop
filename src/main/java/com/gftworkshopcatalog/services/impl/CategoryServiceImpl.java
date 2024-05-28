package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.*;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.CategoryRepository;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;


import java.util.*;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;

    }

    public List<CategoryEntity> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
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
        categoryRepository.delete(categoryEntity);
    }

}


