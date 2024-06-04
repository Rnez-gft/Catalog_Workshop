package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;

public interface CategoryService {

    CategoryEntity addCategory(CategoryEntity categoryEntity);

    List<CategoryEntity> getAllCategories();

    void deleteCategoryById(long categoryId);

    CategoryEntity findCategoryById(long categoryId);

    List<ProductEntity> findProductsByCategoryId(Long categoryId);

    List<ProductEntity> findProductsByCategoryIdAndName(Long categoryId, String namePref);

}
