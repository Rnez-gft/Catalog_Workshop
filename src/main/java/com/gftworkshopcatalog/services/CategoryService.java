package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;

public interface CategoryService {
    public List<ProductEntity> findAllCategorized(long categoryId);

    public CategoryEntity addCategory(CategoryEntity categoryEntity);

    public List<CategoryEntity> getAllCategories();

    public void deleteCategoryById(long id);

    public CategoryEntity findCategoryById(long categoryId);

}
