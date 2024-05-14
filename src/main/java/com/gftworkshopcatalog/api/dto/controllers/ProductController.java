package com.gftworkshopcatalog.api.dto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.services.ProductService;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/catalog/products")
public class ProductController {

	private ProductService productService;

    public ProductController(ProductService productService) {
		super();
		this.productService = productService;
	}
    
    @GetMapping
    public ResponseEntity<?> listAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable("productId") long productId) {
        return ResponseEntity.ok(productService.findProductById(productId));
    }
    
    @PostMapping
    public ResponseEntity<?> addNewProduct(@RequestBody Product product) {
        Product createdProduct = productService.addProduct(product);
        System.out.println(product.toString()+"\n\n");
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") long productId, @RequestBody Product product) {
            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/price")
    public ResponseEntity<?> updateProductPrice(@PathVariable("productId") long productId, @RequestParam("newPrice") double newPrice) {
        try {
            Product updatedProduct = productService.updateProductPrice(productId, newPrice);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product price: " + e.getMessage());
        }
    }


    @PatchMapping("/{productId}/stock")
    public ResponseEntity<?> updateProductStock(@PathVariable("productId") long productId, @RequestParam("newStock") long newStock) {
        try {
            Product updatedProduct = productService.updateProductStock(productId, newStock);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product price: " + e.getMessage());
        }
    }
}