package com.gftworkshopcatalog;


import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControlerTestMockMvc {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
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
        Product product = new Product();
        when(productService.addProduct(product)).thenReturn(product);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\",\"description\":\"Test Description\",\"price\":10.0,\"category_Id\":1,\"weight\":2.0,\"current_stock\":100,\"min_stock\":10}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetProductDetails() throws Exception {
        // Given
        long productId = 1L;
        Product product = new Product();
        when(productService.findProductById(productId)).thenReturn(product);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.get("/catalog/products/{productId}", productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Given
        long productId = 1L;
        Product product = new Product();
        when(productService.updateProduct(productId, product)).thenReturn(product);

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.put("/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product\",\"description\":\"Updated Description\",\"price\":20.0,\"category_Id\":1,\"weight\":2.0,\"current_stock\":100,\"min_stock\":10}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Given
        long productId = 1L;

        // When, Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/catalog/products/{productId}", productId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
