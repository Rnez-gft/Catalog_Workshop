package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;

public interface CategoryService {

    public CategoryEntity addCategory(CategoryEntity categoryEntity);

    public List<CategoryEntity> getAllCategories();

    public void deleteCategoryById(long id);

    public CategoryEntity findCategoryById(long categoryId);

    public List<ProductEntity> findProductsByCategoryId(Long categoryId);

}
