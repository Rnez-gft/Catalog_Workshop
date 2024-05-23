package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.exceptions.NotFoundCategory;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
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

    public List<ProductEntity> findAllCategorized(long categoryId) {
        try {
            CategoryEntity categoryEntity = findCategoryById(categoryId);
            return productRepository.findByCategoryId(categoryId);
        } catch (EntityNotFoundException ex) {
            log.error("Category not found with ID: {}", categoryId);
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
    }

    public CategoryEntity addCategory(CategoryEntity categoryEntity) {
        if (categoryEntity.getName() == null || categoryEntity.getCategoryId() < 0) {
            throw new AddProductInvalidArgumentsExceptions("Category details must not contain null or negative values");
        }

        try {
            return categoryRepository.save(categoryEntity);
        } catch (DataAccessException ex) {
            log.error("Failed to save category", ex);
            throw new RuntimeException("Failed to save category", ex);
        }
    }

    public CategoryEntity updateCategory(long categoryId, CategoryEntity categoryEntityDetails) {
        CategoryEntity existingCategory = findCategoryById(categoryId);
        updateCategoryEntity(existingCategory, categoryEntityDetails);
        try {
            return categoryRepository.save(existingCategory);
        } catch (DataAccessException ex) {
            log.error("Failed to update category", ex);
            throw new RuntimeException("Failed to update category", ex);
        }
    }

    private void updateCategoryEntity(CategoryEntity existingCategory, CategoryEntity newDetails) {
        existingCategory.setName(newDetails.getName());
        existingCategory.setProducts(newDetails.getProducts());
    }

    public void deleteCategoryById(long categoryId) {
        CategoryEntity categoryEntity = findCategoryById(categoryId);
        try {
            categoryRepository.delete(categoryEntity);
            log.info("Deleted category with ID: {}", categoryId);
        } catch (EmptyResultDataAccessException ex) {
            log.error("Category not found with ID: {}", categoryId);
            throw new NotFoundCategory("Category not found with ID: " + categoryId);
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
    }
}


