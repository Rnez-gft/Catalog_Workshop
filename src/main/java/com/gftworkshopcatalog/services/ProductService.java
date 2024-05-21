package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.ProductEntity;

import java.util.List;

public interface ProductService {

    public List<ProductEntity> findAllProducts();
    public ProductEntity findProductById(long productId);
    public ProductEntity addProduct(ProductEntity productEntity);
    public ProductEntity updateProduct(Long productId, ProductEntity productEntityDetails);
    public void deleteProduct(long productId);
    public ProductEntity updateProductPrice(long productId, double newPrice);
    public ProductEntity updateProductStock(long productId, int newStock);
}
