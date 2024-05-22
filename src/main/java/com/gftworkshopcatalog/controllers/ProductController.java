package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.BadRequest;
import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.exceptions.InternalServerError;
import com.gftworkshopcatalog.exceptions.NotFoundProduct;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Everything about the products")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;

    public ProductController(ProductServiceImpl productServiceImpl) {
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
            List<ProductEntity> products = productServiceImpl.findAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> addNewProduct( @RequestBody ProductEntity productEntity) {
        try {
            ProductEntity createdProductEntity = productServiceImpl.addProduct(productEntity);
            return new ResponseEntity<>(createdProductEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details", description = "Returns details of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product details",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> getProductDetails(@PathVariable long id) {
        try {
            ProductEntity productEntity = productServiceImpl.findProductById(id);
            return ResponseEntity.ok(productEntity);
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductEntity productEntity) {
        try {
            ProductEntity updatedProductEntity = productServiceImpl.updateProduct(id, productEntity);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> deleteProduct(@PathVariable long id) {
        try {
            productServiceImpl.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Update the price of a product", description = "Partially updates the price of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> updateProductPrice(@PathVariable long id, @RequestParam double newPrice) {
        try {
            ProductEntity updatedProductEntity = productServiceImpl.updateProductPrice(id, newPrice);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found with ID: " + id, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }catch (BadRequest e) {
            ErrorResponse errorResponse = new ErrorResponse("Product details must not be null except description", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update the stock of a product", description = "Partially updates the stock of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> updateProductStock(@PathVariable long id, @RequestParam int newStock) {
        try {
            ProductEntity updatedProductEntity = productServiceImpl.updateProductStock(id, newStock);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (NotFoundProduct e) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found with ID: " + id, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/ids")
    @Operation(summary = "Get products by IDs", description = "Returns a list of products for the given list of IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> listProductsById(@RequestBody List<Long> ids) {
        try {
            List<ProductEntity> products = productServiceImpl.findProductsByIds(ids);
            return ResponseEntity.ok(products);
        } catch (EntityNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse("One or more product IDs not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
