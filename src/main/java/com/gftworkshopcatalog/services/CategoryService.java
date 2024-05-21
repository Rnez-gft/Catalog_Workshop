package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;

public interface CategoryService {
    public List<ProductEntity> findAllCategorized(int categoryId);

    public CategoryEntity addCategory(CategoryEntity categoryEntity);


}
