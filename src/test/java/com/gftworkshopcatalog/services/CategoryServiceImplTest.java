package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.CategoryRepository;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.services.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllCategorized_Success() {
        int categoryId = 1;
        CategoryEntity category = new CategoryEntity();
        category.setCategoryId(categoryId);
        ProductEntity product = new ProductEntity();
        //product.category(categoryId);

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(Collections.singletonList(product));

        List<ProductEntity> result = categoryService.findAllCategorized(categoryId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategory_Id());
    }

    @Test
    void testFindAllCategorized_CategoryNotFound() {
        int categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.findAllCategorized(categoryId));
    }

    @Test
    void testFindAllCategorized_DataAccessException() {
        int categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenThrow(DataAccessException.class);

        assertThrows(RuntimeException.class, () -> categoryService.findAllCategorized(categoryId));
    }

    @Test
    void testAddCategory_Success() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Electronics");

        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(category);

        CategoryEntity result = categoryService.addCategory(category);
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
    }

    @Test
    void testAddCategory_InvalidArgument() {
        CategoryEntity category = new CategoryEntity();

        assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(category));
    }

    @Test
    void testAddCategory_DataAccessException() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Electronics");

        when(categoryRepository.save(any(CategoryEntity.class))).thenThrow(DataAccessException.class);

        assertThrows(RuntimeException.class, () -> categoryService.addCategory(category));
    }
}
