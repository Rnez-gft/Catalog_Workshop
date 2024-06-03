package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.OrdersDTO;
import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.ProductService;
import com.gftworkshopcatalog.services.RelatedProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/RelatedProducts")
@Tag(name = "RelatedProducts", description = "Everything about related products")
public class RelatedProductsController {



        private final ProductService productService;
        private final RelatedProductsService relatedProductsService;

        public RelatedProductsController(ProductService productService, RelatedProductsService relatedProductsService) {
                this.productService = productService;
                this.relatedProductsService = relatedProductsService;
        }


        @GetMapping("/{userId}")
        @Operation(summary = "List all products", description = "Returns a list of all products.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Product list",
                        content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
                @ApiResponse(responseCode = "404", description = "Error response",
                        content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
                @ApiResponse(responseCode = "500", description = "Error response",
                        content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
        })


        public ResponseEntity<List<ProductEntity>> getRelatedProducts(@PathVariable Long userId) {
                Optional<OrdersDTO> orders = relatedProductsService.getLatestOrder(userId);
                List<ProductEntity> relatedProducts = productService.findRelatedProducts(orders);
                return ResponseEntity.ok(relatedProducts);
        }
    }



