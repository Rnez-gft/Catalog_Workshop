package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.exceptions.ErrorResponse;
import com.gftworkshopcatalog.model.CategoryEntity;
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
import static org.mockito.Mockito.*;

class CategoryEntityControllerTest {
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
        CategoryEntity categoryEntity1 = new CategoryEntity(1L, "Electronics");
        CategoryEntity categoryEntity2 = new CategoryEntity(2L, "Clothing");
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
        CategoryEntity categoryEntityToAdd = new CategoryEntity(1L, "Electronics");
        CategoryEntity addedCategoryEntity = new CategoryEntity(1L, "Electronics");

        when(categoryServiceImpl.addCategory(categoryEntityToAdd)).thenReturn(addedCategoryEntity);

        ResponseEntity<?> responseEntity = categoryController.addNewCategory(categoryEntityToAdd);

        assertEquals(addedCategoryEntity, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @DisplayName("Server Error addNewCategory()")
    @Test
    void test_addNewCategory_InternalServerError() {
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Electronics");

        when(categoryServiceImpl.addCategory(categoryEntity)).thenThrow(new ServiceException("Internal Server Error"));

        ResponseEntity<?> responseEntity = categoryController.addNewCategory(categoryEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Add New Category - Bad Request on Invalid Input")
    void testAddNewCategory_BadRequest() {
        CategoryEntity categoryEntityToAdd = new CategoryEntity(1L, ""); // Assume empty name is invalid
        when(categoryServiceImpl.addCategory(categoryEntityToAdd)).thenThrow(new IllegalArgumentException("Invalid category details"));

        ResponseEntity<?> response = categoryController.addNewCategory(categoryEntityToAdd);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, ((ErrorResponse) response.getBody()).getStatus());
        assertEquals("Bad request", ((ErrorResponse) response.getBody()).getMessage());
    }

    @Test
    void test_deleteCategoryById() {
        long categoryId = 1L;

        doNothing().when(categoryServiceImpl).deleteCategoryById(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategoryById(categoryId);

        verify(categoryServiceImpl, times(1)).deleteCategoryById(categoryId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Category not found when trying to delete")
    void testDeleteCategoryById_NotFound() {
        long categoryId = 1L;

        doThrow(new EntityNotFoundException("Category not found")).when(categoryServiceImpl).deleteCategoryById(categoryId);

        ResponseEntity<?> response = categoryController.deleteCategoryById(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
        assertEquals("Category not found", ((ErrorResponse) response.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((ErrorResponse) response.getBody()).getStatus());
    }

    @Test
    @DisplayName("Server Error deleteCategoryById()")
    void test_deleteCategoryById_InternalServerError() {
        long categoryId = 1L;

        doThrow(new ServiceException("Internal Server Error")).when(categoryServiceImpl).deleteCategoryById(categoryId);

        ResponseEntity<?> responseEntity = categoryController.deleteCategoryById(categoryId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
