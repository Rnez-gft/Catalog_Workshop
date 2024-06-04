package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.exceptions.SuccessResponse;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@Tag(name = "Promotions", description = "Everything about the promotions")
public class PromotionController {
    private final PromotionServiceImpl promotionService;

    public PromotionController(PromotionServiceImpl promotionService) {
        super();
        this.promotionService = promotionService;
    }
    @GetMapping
    @Operation(summary = "List all promotions", description = "Returns a list of all promotions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promotion list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<List<PromotionEntity>> getAllPromotions() {
        List<PromotionEntity> promotions = promotionService.findAllPromotions();
        return ResponseEntity.ok(promotions);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get promotions details", description = "Returns details of a specific promotion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promotion details",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Promotion not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<PromotionEntity> getPromotionsById(@Parameter(description = "Promotion ID")@PathVariable Long id) {
        PromotionEntity promotion = promotionService.findPromotionById(id);
        return ResponseEntity.ok(promotion);
    }
    @PostMapping
    @Operation(summary = "Add a new promotion", description = "Creates a new promotion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promotion created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<PromotionEntity> addPromotion(@RequestBody PromotionEntity promotionEntity) {
        PromotionEntity createdPromotion = promotionService.addPromotion(promotionEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update a promotion", description = "Updates the details of a specific promotion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promotion updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Promotion bad request",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Promotion not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<PromotionEntity> updatePromotion(@Parameter(description = "Promotion ID")@PathVariable Long id, @RequestBody PromotionEntity promotionDetails){
        PromotionEntity updatedPromotion = promotionService.updatePromotion(id, promotionDetails);
        return ResponseEntity.ok(updatedPromotion);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a promotion", description = "Deletes a specific promotion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Promotion deleted",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Promotion not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<Void> deletePromotion(@Parameter(description = "Promotion ID")@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

}
