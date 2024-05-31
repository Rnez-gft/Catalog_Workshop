package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.exceptions.*;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.CategoryRepository;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionRepository promotionRepository;
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

        when(categoryRepository.save(categoryEntity)).thenThrow(new DataIntegrityViolationException("Database access error") {});


        assertThrows(RuntimeException.class, () -> categoryServiceImpl.addCategory(categoryEntity),
                "Expected RuntimeException when database error occurs");

    }


    @Test
    @DisplayName("Delete Category: Success")
    void testDeleteCategory() {
        when(categoryRepository.findById(categoryEntity.getCategoryId())).thenReturn(Optional.of(categoryEntity));
        when(productRepository.findByCategoryId(categoryEntity.getCategoryId())).thenReturn(List.of());
        when(promotionRepository.findByCategoryId(categoryEntity.getCategoryId())).thenReturn(List.of());

        categoryServiceImpl.deleteCategoryById(categoryEntity.getCategoryId());

        verify(categoryRepository, times(1)).delete(categoryEntity);
    }

    @Test
    @DisplayName("Delete Category: Throw EntityNotFoundException when deleting non-existing category")
    void testDeleteCategory_NotFound() {
        long nonExistentCategoryId = 99L;

        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        doThrow(EmptyResultDataAccessException.class).when(categoryRepository).delete(any());

        NotFoundCategory exception = assertThrows(NotFoundCategory.class, () -> categoryServiceImpl.deleteCategoryById(nonExistentCategoryId));

        assertEquals("Category not found with ID: " + nonExistentCategoryId, exception.getMessage(), "The exception message should be 'Category not found with ID: " + nonExistentCategoryId + "'");

        verify(categoryRepository, never()).deleteById(nonExistentCategoryId);
    }

    @Test
    @DisplayName("Delete Category: Handle DataAccessException")
    void testDeleteCategory_DataAccessException() {
        when(categoryRepository.findById(categoryEntity.getCategoryId())).thenReturn(Optional.of(categoryEntity));
        when(productRepository.findByCategoryId(categoryEntity.getCategoryId())).thenReturn(List.of());
        when(promotionRepository.findByCategoryId(categoryEntity.getCategoryId())).thenReturn(List.of());
        
        doThrow(new InternalServiceException("Failed to delete category with ID: " + categoryEntity.getCategoryId())).when(categoryRepository).delete(categoryEntity);

        InternalServiceException exception = assertThrows(InternalServiceException.class, () -> categoryServiceImpl.deleteCategoryById(categoryEntity.getCategoryId()));

        assertEquals("Failed to delete category with ID: " + categoryEntity.getCategoryId(), exception.getMessage(), "The exception message should be 'Failed to delete category with ID: " + categoryEntity.getCategoryId());
    }

    @Test
    @DisplayName("Find products by category ID: Success")
    void testFindProductsByCategoryId_Success() {
        Long categoryId = 1L;
        List<ProductEntity> products = new ArrayList<>();
        products.add(new ProductEntity());
        products.add(new ProductEntity());

        when(productRepository.findByCategoryId(categoryId)).thenReturn(products);

        List<ProductEntity> result = categoryServiceImpl.findProductsByCategoryId(categoryId);

        assertNotNull(result, "The result should not be null");
        assertEquals(products.size(), result.size(), "The size of result should match the size of products");
        assertTrue(result.containsAll(products), "The result should contain all products");
    }

    @Test
    @DisplayName("Find products by category ID: Empty list")
    void testFindProductsByCategoryId_EmptyList() {
        Long categoryId = 1L;
        List<ProductEntity> products = Collections.emptyList();

        when(productRepository.findByCategoryId(categoryId)).thenReturn(products);

        NotFoundCategory exception = assertThrows(NotFoundCategory.class, () -> categoryServiceImpl.findProductsByCategoryId(categoryId),
                "Expected findProductsByCategoryId to throw, but it did not");

        assertTrue(exception.getMessage().contains("Category not found with ID: " + categoryId), "The exception message should indicate category not found");
    }


    @Test
    @DisplayName("Find products by category ID and name: Success")
    void testFindProductsByCategoryIdAndName_Success() {
        Long categoryId = 10L;
        String name = "pro";
        String formattedName = categoryServiceImpl.formatName(name);
        List<ProductEntity> products = new ArrayList<>();
        products.add(new ProductEntity(1L, "Product1", "Description1", 10.0, 10L, 1.0, 100, 10));
        products.add(new ProductEntity(2L, "Product2", "Description2", 20.0, 10L, 2.0, 200, 20));

        when(productRepository.findByCategoryIdAndNameStartsWith(categoryId, formattedName)).thenReturn(products);

        List<ProductEntity> result = categoryServiceImpl.findProductsByCategoryIdAndName(categoryId, name);

        assertNotNull(result, "The result should not be null");
        assertEquals(products.size(), result.size(), "The size of the result should match the size of products");
        assertTrue(result.containsAll(products), "The result should contain all products");
    }

    @Test
    @DisplayName("Find products by category ID and name: Empty list")
    void testFindProductsByCategoryIdAndName_EmptyList() {
        Long categoryId = 1L;
        String name = "products";
        String formattedName = categoryServiceImpl.formatName(name);;

        when(productRepository.findByCategoryIdAndNameStartsWith(categoryId, formattedName)).thenReturn(Collections.emptyList());

        NotFoundCategory exception = assertThrows(NotFoundCategory.class, () -> categoryServiceImpl.findProductsByCategoryIdAndName(categoryId, name),
                "Expected findProductsByCategoryIdAndName to throw, but it did not");

        assertEquals("Category not found with ID: " + categoryId + " and NAME: " + formattedName, exception.getMessage(),
                "The exception message should indicate category not found");
    }

    @Test
    @DisplayName("Format name: Correct formatting")
    void testFormatName() {
        String name = "example";
        String formattedName = categoryServiceImpl.formatName(name);

        assertEquals("Example%", formattedName, "The formatted name should be 'Example%'");
    }
}

