package com.gftworkshopcatalog.services.impl;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
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
            return new EntityNotFoundException("Category not found with ID: " + categoryId);
        });
    }

    public List<ProductEntity> findAllCategorized(long categoryId) {
        try {
            CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category with ID " + categoryId + " not found"));

            return productRepository.findByCategory_CategoryId(categoryId);

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


    public void deleteCategoryById(long categoryId) {
        CategoryEntity categoryEntity = findCategoryById(categoryId);
        try {
            categoryRepository.delete(categoryEntity);
            log.info("Deleting category with ID: {}", categoryId);
        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException("Category with ID: " + categoryId + " not found");
        } catch (DataAccessException ex) {
            log.error("Error accessing data from database", ex);
            throw new RuntimeException("Error accessing data from database", ex);
        }
    }


}


