package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.api.dto.OrderDTO;
import com.gftworkshopcatalog.api.dto.OrdersDTO;
import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {

    List<ProductEntity> findAllProducts();
    ProductEntity findProductById(long productId);
    ProductEntity addProduct(ProductEntity productEntity);
    ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails);
    void deleteProduct(long productId);
    ProductEntity updateProductPrice(long productId, double newPrice);
    ProductEntity updateProductStock(long productId, int newStock);
    List<ProductEntity> findProductsByCategoryIds(Set<Long> categoryIds);
    List<ProductEntity> findRelatedProducts(Optional<OrdersDTO> orders);
}
