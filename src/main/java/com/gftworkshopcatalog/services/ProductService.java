package com.gftworkshopcatalog.services;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        super();
        this.productRepository = productRepository;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        System.out.println(product.toString());
        return productRepository.save(product);
    }

    public Product findProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    public Product updateProduct(Long productId, Product productDetails) {
        Product product = productRepository.findById(productId).orElse(null);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setWeight(productDetails.getWeight());

        return productRepository.save(product);
    }

    public void deleteProduct(long productId) {
        Product product = findProductById(productId);
        productRepository.delete(product);
    }

    public Product updateProductPrice(long productId, double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        Optional<Product> productOptional = productRepository.findById(productId);

        if (!productOptional.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOptional.get();

        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    public Product updateProductStock(long productId, long newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOptional.get();
        return productRepository.save(product);
    }
}