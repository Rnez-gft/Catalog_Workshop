package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Everything about the categories")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;

    public CategoryController(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns a list of all categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category list",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryEntity.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryEntity> categories = categoryServiceImpl.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
    public ResponseEntity<?> addNewCategory(@RequestBody CategoryEntity categoryEntity) {
        try {
            CategoryEntity createdCategoryEntity = categoryServiceImpl.addCategory(categoryEntity);
            return new ResponseEntity<>(createdCategoryEntity, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Bad request", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category details", description = "Returns details of a specific category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category details",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryEntity.class)) }),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Error response",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    })
    public ResponseEntity<?> findAllCategorized(
            @Parameter(description = "Category ID", required = true)
            @PathVariable("id") int id) {
        try {
            List<ProductEntity> products = categoryServiceImpl.findAllCategorized(id);
            return ResponseEntity.ok(products);
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Category not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
    public ResponseEntity<?> deleteCategoryById(@Parameter(description = "Category ID") @PathVariable("id") int id) {
        try {
            categoryServiceImpl.deleteCategoryById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Category not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}