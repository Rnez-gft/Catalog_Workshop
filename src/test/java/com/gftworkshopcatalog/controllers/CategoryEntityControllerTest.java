package com.gftworkshopcatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshopcatalog.exceptions.*;
import com.gftworkshopcatalog.model.CategoryEntity;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryEntityControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private CategoryServiceImpl categoryService;
    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void test_listAllCategories() throws Exception {
        List<CategoryEntity> mockCategoryEntities = Arrays.asList(
                new CategoryEntity(1L, "Electronics"),
                new CategoryEntity(2L, "Clothing")
        );
        when(categoryService.getAllCategories()).thenReturn(mockCategoryEntities);
            mockMvc.perform(get("/categories")
                .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].categoryId").value(mockCategoryEntities.get(0).getCategoryId()))
                    .andExpect(jsonPath("$[0].name").value(mockCategoryEntities.get(0).getName()))
                    .andExpect(jsonPath("$[1].categoryId").value(mockCategoryEntities.get(1).getCategoryId()))
                    .andExpect(jsonPath("$[1].name").value(mockCategoryEntities.get(1).getName()));
        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("List all Categories - InternalServiceException")
    void listAllPromotions_Failure() throws Exception {
        when(categoryService.getAllCategories()).thenThrow(new InternalServiceException("Database error"));
        mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Database error"));
        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("Add new category - Success")
    void test_addNewCategory() throws Exception {
        CategoryEntity categoryEntityToAdd = new CategoryEntity(1L, "Electronics");

        when(categoryService.addCategory(any(CategoryEntity.class))).thenReturn(categoryEntityToAdd);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryEntityToAdd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(categoryEntityToAdd.getCategoryId()))
                .andExpect(jsonPath("$.name").value(categoryEntityToAdd.getName()));
    }


    @Test
    @DisplayName("Add new category - BadRequest")
    void whenPostCategory_thenFailDueToInvalidData() throws Exception {
        CategoryEntity categoryEntityToAdd = new CategoryEntity(1L, "");

        when(categoryService.addCategory(any(CategoryEntity.class))).thenThrow(new BadRequest("Invalid category data"));
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryEntityToAdd)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid category data"));
        verify(categoryService).addCategory(any(CategoryEntity.class));
    }

    @Test
    void test_deleteCategoryById() throws Exception {
        long categoryId = 1L;

        doNothing().when(categoryService).deleteCategoryById(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
        verify(categoryService).deleteCategoryById(categoryId);
    }


    @Test
    @DisplayName("Delete a category - NotFoundCategory")
    void deletePromotion_NotFound() throws Exception {
        Long categoryId = 999L;

        doThrow(new NotFoundPromotion("Promotion not found with ID: " + categoryId))
                .when(categoryService).deleteCategoryById(categoryId);
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Promotion not found with ID: " + categoryId));
        verify(categoryService).deleteCategoryById(categoryId);
    }


    @Test
    void testFindProductsByCategoryId_CategoryExists() {
        Long categoryId = 1L;
        List<ProductEntity> products = Arrays.asList(new ProductEntity(), new ProductEntity());
        when(categoryService.findProductsByCategoryId(categoryId)).thenReturn(products);

        ResponseEntity<?> responseEntity = categoryController.listProductsByCategoryId(categoryId);

        assertEquals(products, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Get promotion details by ID - Success")
    void getPromotionById_Success() throws Exception {

        CategoryEntity category = new CategoryEntity(1L, "products");

        List<ProductEntity> products = Arrays.asList(
                new ProductEntity(1L, "Updated Product", "Updated Description", 55.55, 1L, 1.0, 100, 10),
                new ProductEntity(2L, "Updated Product2", "Updated Description2", 65.55, 1L, 2.0, 200, 20)
        );

        when(categoryService.findProductsByCategoryId(category.getCategoryId())).thenReturn(products);
        mockMvc.perform(get("/categories/{id}/products", category.getCategoryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(products.get(0).getCategoryId()))
                .andExpect(jsonPath("$[1].categoryId").value(products.get(1).getCategoryId()))
                .andExpect(jsonPath("$[0].name").value(products.get(0).getName()))
                .andExpect(jsonPath("$[1].name").value(products.get(1).getName()));

        verify(categoryService).findProductsByCategoryId(category.getCategoryId());
    }


    @Test
    @DisplayName("Get Category details by ID - NotFound")
    void testFindProductsByCategoryId_NotFound() throws Exception {
        Long categoryId = 999L;

        when(categoryService.findProductsByCategoryId(categoryId)).thenThrow(new NotFoundCategory("Category not found with ID: " + categoryId));
        mockMvc.perform(get("/categories/{id}/products", categoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with ID: " + categoryId));
        verify(categoryService).findProductsByCategoryId(categoryId);
    }

    @Test
    @DisplayName("List products by category ID and name - Success")
    void test_listProductsByCategoryIdAndName_Success() throws Exception {
        Long categoryId = 1L;
        String name = "Electronics";
        List<ProductEntity> products = Arrays.asList(
                new ProductEntity(1L, "Electronics Product1", "Description1", 100.0, 1L, 1.0, 100, 10),
                new ProductEntity(2L, "Electronics Product2", "Description2", 200.0, 1L, 2.0, 200, 20)
        );

        when(categoryService.findProductsByCategoryIdAndName(categoryId, name)).thenReturn(products);

        mockMvc.perform(get("/categories/{categoryId}/{name}/products", categoryId, name)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId").value(products.get(0).getCategoryId()))
                .andExpect(jsonPath("$[0].name").value(products.get(0).getName()))
                .andExpect(jsonPath("$[1].categoryId").value(products.get(1).getCategoryId()))
                .andExpect(jsonPath("$[1].name").value(products.get(1).getName()));

        verify(categoryService).findProductsByCategoryIdAndName(categoryId, name);
    }

    @Test
    @DisplayName("List products by category ID and name - NotFound")
    void test_listProductsByCategoryIdAndName_NotFound() throws Exception {
        Long categoryId = 999L;
        String name = "NonExistent";

        when(categoryService.findProductsByCategoryIdAndName(categoryId, name)).thenThrow(new NotFoundCategory("Category not found with ID: " + categoryId + " and NAME: " + name));

        mockMvc.perform(get("/categories/{categoryId}/{name}/products", categoryId, name)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found with ID: " + categoryId + " and NAME: " + name));

        verify(categoryService).findProductsByCategoryIdAndName(categoryId, name);
    }


}



