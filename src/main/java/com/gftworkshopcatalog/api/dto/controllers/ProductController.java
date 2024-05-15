package com.gftworkshopcatalog.api.dto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.services.ProductService;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Everything about the products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all products", description = "Returns a list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> listAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @PostMapping
    @Operation(summary = "Add a new product", description = "Creates a new product in the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> addNewProduct(
            @RequestBody Product product) {
        Product createdProduct = productService.addProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details", description = "Returns details of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product details",
                    content = { @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> getProductDetails(
            @Parameter(description = "Product ID", required = true)
            @PathVariable("id") long productId) {
        Product product = productService.findProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates the details of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable("id") Long productId,
            @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a specific product from the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable("id") long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/recommendations")
    @Operation(summary = "Get related products", description = "Returns a list of products related to a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of related products",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> getRelatedProducts(
            @Parameter(description = "Product ID", required = true)
            @PathVariable("id") long productId) {
        return ResponseEntity.ok().build();
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
    public ResponseEntity<?> updateProductStock(@PathVariable("productId") long productId, @RequestParam("newStock") int newStock) {
        try {
            Product updatedProduct = productService.updateProductStock(productId, newStock);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product price: " + e.getMessage());
        }
    }

    public class ErrorResponse {
        private String message;
        private int errorCode;

        public ErrorResponse(String message, int errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }

        // Getters y setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }
    }

    public class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}