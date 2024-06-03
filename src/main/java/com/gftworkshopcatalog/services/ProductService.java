package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.api.dto.CartProductDTO;
import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;

public interface ProductService {

    List<ProductEntity> findAllProducts();
    ProductEntity findProductById(long productId);
    List<ProductEntity> findProductsByIds(List<Long> ids);
    ProductEntity addProduct(ProductEntity productEntity);
    ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails);
    void deleteProduct(long productId);
    ProductEntity updateProductPrice(long productId, double newPrice);
    ProductEntity updateProductStock(long productId, int newStock);
    List<ProductEntity> calculateListDiscountedPrice(List<CartProductDTO> cartProducts);
}
