package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.CategoryService;
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

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        super();
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> findAllCategories() {
        List<CategoryEntity> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryEntity> addNewCategory(@RequestBody CategoryEntity categoryEntity) {
        CategoryEntity createdCategory = categoryService.addCategory(categoryEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }



    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntity> findAllCategorized(@PathVariable Long id) {
        List<ProductEntity> products = categoryService.findAllCategorized(id);
        return ResponseEntity.ok((CategoryEntity) products);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}