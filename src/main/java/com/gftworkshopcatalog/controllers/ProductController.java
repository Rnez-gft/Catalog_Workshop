package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.CartProductDTO;
import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.exceptions.SuccessResponse;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ProductEntity>> listAllProducts() {
            List<ProductEntity> products = productServiceImpl.findAllProducts();
            return ResponseEntity.ok(products);
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
    public ResponseEntity<ProductEntity> addNewProduct( @RequestBody ProductEntity productEntity) {
            ProductEntity createdProductEntity = productServiceImpl.addProduct(productEntity);
            return new ResponseEntity<>(createdProductEntity, HttpStatus.CREATED);
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
    public ResponseEntity<ProductEntity> getProductDetails(@Parameter(description = "Product ID")@PathVariable Long id) {
            ProductEntity productEntity = productServiceImpl.findProductById(id);
            return ResponseEntity.ok(productEntity);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates the details of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<ProductEntity> updateProduct(@Parameter(description = "Product ID")@PathVariable Long id, @RequestBody ProductEntity productEntity) {
            ProductEntity updatedProductEntity = productServiceImpl.updateProduct(id, productEntity);
            return ResponseEntity.ok(updatedProductEntity);
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
    public ResponseEntity<ProductEntity> deleteProduct(@Parameter(description = "Product ID")@PathVariable Long id) {
            productServiceImpl.deleteProduct(id);
            return ResponseEntity.noContent().build();
    }

    @PatchMapping("/newPrice/{id}/{newPrice}")
    @Operation(summary = "Update the price of a product", description = "Partially updates the price of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<ProductEntity> updateProductPrice(@Parameter(description = "Product ID")@PathVariable Long id, @Parameter(description = "New price to update the current one")@RequestParam double newPrice) {
            ProductEntity updatedProductEntity = productServiceImpl.updateProductPrice(id, newPrice);
            return ResponseEntity.ok(updatedProductEntity);
    }

    @PatchMapping("/newStock/{id}/{quantity}")
    @Operation(summary = "Update the stock of a product", description = "Partially updates the stock of a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<ProductEntity> updateProductStock(@Parameter(description = "Product ID")@PathVariable Long id, @Parameter(description = "Quantity of the stock to change")@RequestParam int quantity) {
            ProductEntity updatedProductEntity = productServiceImpl.updateProductStock(id, quantity);
            return ResponseEntity.ok(updatedProductEntity);
    }


    @PostMapping("/byIds")
    @Operation(summary = "Get products by IDs", description = "Returns a list of products for the given list of IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<List<ProductEntity>> listProductsById(@RequestBody List<Long> ids) {
            List<ProductEntity> products = productServiceImpl.findProductsByIds(ids);
            return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}/{quantity}")
    @Operation(summary = "Get the product price at checkout", description = "Gets the product price based on volume promotions during checkout.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price successfully retrieved",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<Double> getPriceProductCheckout(@Parameter(description = "Product ID")@PathVariable Long id, @Parameter(description = "Quantity of a product")@RequestParam int quantity) {
            double discountedPrice = productServiceImpl.calculateDiscountedPrice(id, quantity);
            return ResponseEntity.ok(discountedPrice);
    }

    @PostMapping("/volumePromotion")
    @Operation(summary = "Get the total price at checkout", description = "Gets the total price based on volume promotions during checkout.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price successfully retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductEntity>> getPriceProductCheckoutV2(
            @Parameter(description = "List of cart products") @RequestBody List<CartProductDTO> cartProducts) {
        List<ProductEntity> discountedProducts = productServiceImpl.calculateListDiscountedPrice(cartProducts);
        return ResponseEntity.ok(discountedProducts);
    }
}
