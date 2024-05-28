package com.gftworkshopcatalog.utils;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.model.ProductEntity;

public class ProductValidationUtils {

    private ProductValidationUtils() {
    }

    public static void validateProductEntity(ProductEntity productEntity) {
        if (productEntity.getName() == null ||
                productEntity.getPrice() == null || productEntity.getPrice() < 0 ||
                productEntity.getCategoryId() == null ||
                productEntity.getWeight() == null || productEntity.getWeight() < 0 ||
                productEntity.getCurrentStock() == null || productEntity.getCurrentStock() < 0 ||
                productEntity.getMinStock() == null || productEntity.getMinStock() < 0) {
            throw new AddProductInvalidArgumentsExceptions("Product details must not be null except description");
        }
    }
}
