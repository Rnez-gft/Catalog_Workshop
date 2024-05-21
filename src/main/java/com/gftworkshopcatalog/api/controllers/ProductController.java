package com.gftworkshopcatalog.api.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gftworkshopcatalog.model.ProductEntity;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Everything about the products")
public class ProductController {

    private ProductServiceImpl productServiceImpl;

    public ProductController(ProductServiceImpl productServiceImpl) {
        super();
        this.productServiceImpl = productServiceImpl;
    }

    @GetMapping
    @Operation(summary = "List all products", description = "Returns a list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> listAllProducts() {
        try {
            return ResponseEntity.ok(productServiceImpl.findAllProducts());
        } catch (Exception  ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @Operation(summary = "Add a new product", description = "Creates a new product in the catalog.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> addNewProduct(@RequestBody ProductEntity productEntity) {
        try {
            ProductEntity createdProductEntity = productServiceImpl.addProduct(productEntity);
            return new ResponseEntity<>(createdProductEntity, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Bad request", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception  ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details", description = "Returns details of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product details",
                    content = { @Content(mediaType = "application/json",schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> getProductDetails(
            @Parameter(description = "Product ID", required = true)
            @PathVariable("id") long productId) {
        try {
            ProductEntity productEntity = productServiceImpl.findProductById(productId);
            return ResponseEntity.ok(productEntity);
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates the details of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable("id") Long productId,
            @RequestBody ProductEntity productEntity) {

        try {
            ProductEntity updatedProductEntity = productServiceImpl.updateProduct(productId, productEntity);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception  ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }

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
        try {
            productServiceImpl.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception  ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);


        }
    }


    @PatchMapping("/{productId}/price")
    @Operation(summary = "Update the price of a product", description = "Partially updates the price of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> updateProductPrice(@PathVariable("productId") long productId, @RequestParam("newPrice") double newPrice) {
        try {
            ProductEntity updatedProductEntity = productServiceImpl.updateProductPrice(productId, newPrice);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product price: " + e.getMessage());
        }
    }

    @PatchMapping("/{productId}/stock")
    @Operation(summary = "Update the stock of a product", description = "Partially updates the stock of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> updateProductStock(@PathVariable("productId") long productId, @RequestParam("newStock") int newStock) {
        try {
            ProductEntity updatedProductEntity = productServiceImpl.updateProductStock(productId, newStock);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product price: " + e.getMessage());
        }
    }

@Generated
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