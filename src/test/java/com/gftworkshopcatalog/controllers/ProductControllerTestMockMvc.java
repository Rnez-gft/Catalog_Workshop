package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.api.dto.controllers.ProductController;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTestMockMvc {

    
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    void testListAllProducts() throws Exception {
        // Given
        List<Product> productList = Arrays.asList(new Product(), new Product());
        when(productService.findAllProducts()).thenReturn(productList);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testAddNewProduct() throws Exception {
        // Given
        Product product = new Product(1L,"Test Product", "Test Description", 10.0, 1, 2.0, 100, 10);
        when(productService.addProduct(product)).thenReturn(product);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Test Product\",\"description\":\"Test Description\",\"price\":10.0,\"category_Id\":1,\"weight\":2.0,\"current_stock\":100,\"min_stock\":10}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetProductDetails() throws Exception {
        // Given
        long productId = 1L;
        Product product = new Product(1L,"Test Product", "Test Description", 10.0, 1, 2.0, 100, 10);
        when(productService.findProductById(productId)).thenReturn(product);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Test Product\",\"description\":\"Test Description\",\"price\":10.0,\"category_Id\":1,\"weight\":2.0,\"current_stock\":100,\"min_stock\":10}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Given
        long productId = 1L;
        Product product = new Product(productId, "Test Product", "Test Description", 10.0, 1, 2.0, 100, 10);
        when(productService.updateProduct(productId, product)).thenReturn(product);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Test Product\",\"description\":\"Test Description\",\"price\":10.0,\"category_Id\":1,\"weight\":2.0,\"current_stock\":100,\"min_stock\":10}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Given
        long productId = 1L;

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", productId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testUpdateProductPrice() throws Exception {
        // Given
        long productId = 1L;
        double newPrice = 20.0;
        Product updatedProduct = new Product(productId, "Test Product", "Test Description", newPrice, 1, 2.0, 100, 10);

        when(productService.updateProductPrice(productId, newPrice)).thenReturn(updatedProduct);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/products/{productId}/price?newPrice={newPrice}", productId, newPrice)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateProductStock() throws Exception {
        // Given
        long productId = 1L;
        int newStock = 200;
        Product updatedProduct = new Product(productId, "Test Product", "Test Description", 10.0, 1, 2.0, newStock, 10);
        when(productService.updateProductStock(productId, newStock)).thenReturn(updatedProduct);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/products/{productId}/stock?newStock={newStock}", productId, newStock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

}
