package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Everything about the categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        super();
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns a list of all categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryEntity.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<List<CategoryEntity>> findAllCategories() {
            List<CategoryEntity> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "Add a new category", description = "Creates a new category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<CategoryEntity> addNewCategory(@RequestBody CategoryEntity categoryEntity) {

            CategoryEntity createdCategoryEntity = categoryService.addCategory(categoryEntity);
            return new ResponseEntity<>(createdCategoryEntity, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a specific category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })

    public ResponseEntity<CategoryEntity> deleteCategoryById(@Parameter(description = "Category ID") @PathVariable("id") long categoryId) {

            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "List all products by category ID", description = "Returns a list of all products for the specified category ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })

    public ResponseEntity<List<ProductEntity>> listProductsByCategoryId(@Parameter(description = "Category ID") @PathVariable("id") Long categoryId) {
            List<ProductEntity> products = categoryService.findProductsByCategoryId(categoryId);
            return ResponseEntity.ok(products);

    }

    @GetMapping("/{id}/{productName}/products")
    @Operation(summary = "List all products by category ID and name",
            description = "Returns a list of all products for the specified category ID and name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })

    public ResponseEntity<?> listProductsByCategoryIdAndName(
            @Parameter(description = "Category ID") @PathVariable("id") Long categoryId,
            @Parameter(description = "First word of the product name") @PathVariable("productName") String name) {

        List<ProductEntity> products = categoryService.findProductsByCategoryIdAndName(categoryId, name);
        return ResponseEntity.ok(products);
    }

}