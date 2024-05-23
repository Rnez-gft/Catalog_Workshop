package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.exceptions.AddProductInvalidArgumentsExceptions;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.CategoryRepository;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoryEntity = CategoryEntity.builder()
                .categoryId(1L)
                .name("Category 1")
                .build();
    }

    @Test
    @DisplayName("Find all categories: Success")
    void findAllCategories() {
        CategoryEntity category2 = CategoryEntity.builder()
                .categoryId(2L)
                .name("Category 2")
                .build();

        List<CategoryEntity> mockCategoryEntities = Arrays.asList(categoryEntity, category2);
        when(categoryRepository.findAll()).thenReturn(mockCategoryEntities);

        List<CategoryEntity> allCategoryEntities = categoryServiceImpl.getAllCategories();

        assertNotNull(allCategoryEntities, "The category list should not be null");
        assertEquals(2, allCategoryEntities.size(), "The category list should contain 2 items");
        assertTrue(allCategoryEntities.contains(categoryEntity), "The list should contain the category with the ID: " + categoryEntity.getCategoryId());
        assertTrue(allCategoryEntities.contains(category2), "The list should contain the category with the ID: " + category2.getCategoryId());
    }

    @Test
    @DisplayName("Find all categories: Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryEntity> allCategoryEntities = categoryServiceImpl.getAllCategories();

        assertNotNull(allCategoryEntities, "The category list should not be null");
        assertTrue(allCategoryEntities.isEmpty(), "The category list should be empty");
    }

    @Test
    @DisplayName("Find all categories: Handles DataAccessException")
    void testFindAllCategoriesDataAccessException() {
        when(categoryRepository.findAll()).thenThrow(new DataAccessException("Database access error") {});

        Exception exception = assertThrows(RuntimeException.class, () -> categoryServiceImpl.getAllCategories(),
                "Expected findAllCategories to throw, but it did not");

        assertTrue(exception.getMessage().contains("Error accessing data from database"));
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertInstanceOf(DataAccessException.class, exception.getCause(), "The cause should be a DataAccessException");
    }

    @Test
    @DisplayName("Find all categorized products by category ID: Success")
    void findAllCategorized_Success() {
        long categoryId = 1L;
        ProductEntity product1 = ProductEntity.builder()
                .id(1L)
                .name("Product 1")
                .description("Description here")
                .price(100.34)
                .category(CategoryEntity.builder().categoryId(categoryId).build())
                .weight(10.5)
                .current_stock(10)
                .min_stock(5)
                .build();

        ProductEntity product2 = ProductEntity.builder()
                .id(2L)
                .name("Product 2")
                .description("Description here 2")
                .price(150.34)
                .category(CategoryEntity.builder().categoryId(categoryId).build())
                .weight(15.5)
                .current_stock(15)
                .min_stock(10)
                .build();

        List<ProductEntity> expectedProducts = Arrays.asList(product1, product2);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(productRepository.findByCategoryId(categoryId)).thenReturn(expectedProducts);

        List<ProductEntity> actualProducts = categoryServiceImpl.findAllCategorized(categoryId);

        assertNotNull(actualProducts, "The categorized product list should not be null");
        assertEquals(2, actualProducts.size(), "The categorized product list should contain 2 items");
        assertTrue(actualProducts.contains(product1), "The list should contain product 1");
        assertTrue(actualProducts.contains(product2), "The list should contain product 2");
    }

    @Test
    @DisplayName("Find all categorized products by category ID: Category not found")
    void findAllCategorized_CategoryNotFound() {
        long categoryId = 999L; // Non-existing category ID

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findAllCategorized(categoryId));

        assertEquals("Category with ID " + categoryId + " not found", exception.getMessage(), "The exception message should indicate category not found");
    }

    @Test
    @DisplayName("Find all categorized products by category ID: Handle DataAccessException")
    void findAllCategorized_DataAccessException() {
        long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(productRepository.findByCategoryId(categoryId)).thenThrow(new DataAccessException("Database access error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryServiceImpl.findAllCategorized(categoryId));

        assertTrue(exception.getMessage().contains("Error accessing data from database"), "The exception message should indicate data access error");
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertInstanceOf(DataAccessException.class, exception.getCause(), "The cause should be a DataAccessException");
    }

    @Test
    @DisplayName("Add Category: Success")
    void testAddCategory_Success() {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryId(1L)
                .name("Category 1")
                .build();

        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);

        CategoryEntity addedCategory = categoryServiceImpl.addCategory(categoryEntity);

        assertNotNull(addedCategory, "The added category should not be null");
        assertEquals(categoryEntity, addedCategory, "The added category should be the same as the input category");
    }

    @Test
    @DisplayName("Add Category: Invalid Arguments")
    void testAddCategory_InvalidArguments() {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryId(1L)
                .name(null)
                .build();

        assertThrows(AddProductInvalidArgumentsExceptions.class, () -> categoryServiceImpl.addCategory(categoryEntity),
                "Expected addCategory to throw when category details contain null values");
    }

    @Test
    @DisplayName("Add Category: Handle DataAccessException")
    void testAddCategory_DataAccessException() {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryId(1L)
                .name("Category 1")
                .build();

        when(categoryRepository.save(categoryEntity)).thenThrow(new DataAccessException("Database access error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryServiceImpl.addCategory(categoryEntity),
                "Expected addCategory to throw, but it did not");

        assertTrue(exception.getMessage().contains("Failed to save category"), "The exception message should indicate failure to save category");
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertInstanceOf(DataAccessException.class, exception.getCause(), "The cause should be a DataAccessException");
    }


    @Test
    @DisplayName("Delete Category: Success")
    void testDeleteCategory() {
        when(categoryRepository.findById(categoryEntity.getCategoryId())).thenReturn(Optional.of(categoryEntity));

        categoryServiceImpl.deleteCategoryById(categoryEntity.getCategoryId());

        verify(categoryRepository, times(1)).delete(categoryEntity);
    }

    @Test
    @DisplayName("Delete Category: Throw EntityNotFoundException when deleting non-existing category")
    void testDeleteCategory_NotFound() {
        long nonExistentCategoryId = 99L; // ID de la categoría que no existe

        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        doThrow(EmptyResultDataAccessException.class).when(categoryRepository).delete(any());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.deleteCategoryById(nonExistentCategoryId));

        assertEquals("Category not found with ID: " + nonExistentCategoryId, exception.getMessage(), "The exception message should be 'Category not found with ID: " + nonExistentCategoryId + "'");

        verify(categoryRepository, never()).deleteById(nonExistentCategoryId);
    }

    @Test
    @DisplayName("Delete Category: Handle DataAccessException")
    void testDeleteCategory_DataAccessException() {
        when(categoryRepository.findById(categoryEntity.getCategoryId())).thenReturn(Optional.of(categoryEntity));
        doThrow(new DataAccessException("...") {}).when(categoryRepository).delete(categoryEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryServiceImpl.deleteCategoryById(categoryEntity.getCategoryId()));

        assertEquals("Error accessing data from database", exception.getMessage(), "The exception message should be 'Error accessing data from database'");
    }
}