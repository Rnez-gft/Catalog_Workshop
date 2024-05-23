package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CategoryControllerTest {
    @Mock
    private CategoryServiceImpl categoryServiceImpl;
    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_listAllCategories() {
        CategoryEntity categoryEntity1 = new CategoryEntity(1, "Electronics");
        CategoryEntity categoryEntity2 = new CategoryEntity(2, "Clothing");
        List<CategoryEntity> mockCategoryEntities = Arrays.asList(categoryEntity1, categoryEntity2);
        when(categoryServiceImpl.getAllCategories()).thenReturn(mockCategoryEntities);

        ResponseEntity<?> responseEntity = categoryController.findAllCategories();

        assertEquals(mockCategoryEntities, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @DisplayName("Server Error listAllCategories()")
    @Test
    void test_listAllCategories_InternalServerError() {
        when(categoryServiceImpl.getAllCategories()).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = categoryController.findAllCategories();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void test_addNewCategory() {
        CategoryEntity categoryEntityToAdd = new CategoryEntity(1, "Electronics");
        CategoryEntity addedCategoryEntity = new CategoryEntity(1, "Electronics");

        when(categoryServiceImpl.addCategory(categoryEntityToAdd)).thenReturn(addedCategoryEntity);

        ResponseEntity<?> responseEntity = categoryController.addNewCategory(categoryEntityToAdd);

        assertEquals(addedCategoryEntity, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @DisplayName("Server Error addNewCategory()")
    @Test
    void test_addNewCategory_InternalServerError() {
        CategoryEntity categoryEntity = new CategoryEntity(1, "Electronics");

        when(categoryServiceImpl.addCategory(categoryEntity)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = categoryController.addNewCategory(categoryEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Add New Category - Bad Request on Invalid Input")
    void testAddNewCategory_BadRequest() {
        CategoryEntity categoryEntityToAdd = new CategoryEntity(1, ""); // Assume empty name is invalid
        when(categoryServiceImpl.addCategory(categoryEntityToAdd)).thenThrow(new IllegalArgumentException("Invalid category details"));

        ResponseEntity<?> response = categoryController.addNewCategory(categoryEntityToAdd);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals(400, ((ErrorResponse) response.getBody()).getErrorCode());
        assertEquals("Bad request", ((ErrorResponse) response.getBody()).getMessage());
    }

    @Test
    void test_findAllCategorized() {
        int categoryId = 1;
        ProductEntity productEntity1 = new ProductEntity(1L, "Laptop", "Powerful computing device", 999.99, categoryId, 3.0, 10, 2);
        ProductEntity productEntity2 = new ProductEntity(2L, "Smartphone", "Mobile communication device", 599.99, categoryId, 0.5, 20, 5);
        List<ProductEntity> mockProductEntities = Arrays.asList(productEntity1, productEntity2);
        when(categoryServiceImpl.findAllCategorized(categoryId)).thenReturn(mockProductEntities);

        ResponseEntity<?> responseEntity = categoryController.findAllCategorized(categoryId);

        assertEquals(mockProductEntities, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Category not found when retrieving categorized products")
    void testFindAllCategorized_NotFound() {
        int categoryId = 1;

        when(categoryServiceImpl.findAllCategorized(categoryId)).thenThrow(new EntityNotFoundException("Category not found"));

        ResponseEntity<?> response = categoryController.findAllCategorized(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Category not found", ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(404, ((ErrorResponse) response.getBody()).getErrorCode());
    }

    @DisplayName("Server Error findAllCategorized()")
    @Test
    void test_findAllCategorized_InternalServerError() {
        int categoryId = 1;

        when(categoryServiceImpl.findAllCategorized(categoryId)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = categoryController.findAllCategorized(categoryId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void test_deleteCategoryById() {
        int categoryId = 1;

        doNothing().when(categoryServiceImpl).deleteCategoryById(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategoryById(categoryId);

        verify(categoryServiceImpl, times(1)).deleteCategoryById(categoryId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Category not found when trying to delete")
    void testDeleteCategoryById_NotFound() {
        int categoryId = 1;

        doThrow(new EntityNotFoundException("Category not found")).when(categoryServiceImpl).deleteCategoryById(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategoryById(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Category not found", ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(404, ((ErrorResponse) response.getBody()).getErrorCode());
    }

    @Test
    @DisplayName("Server Error deleteCategoryById()")
    void test_deleteCategoryById_InternalServerError() {
        int categoryId = 1;

        doThrow(new ServiceException("Internal Server Error")).when(categoryServiceImpl).deleteCategoryById(categoryId);

        ResponseEntity<?> responseEntity = categoryController.deleteCategoryById(categoryId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
