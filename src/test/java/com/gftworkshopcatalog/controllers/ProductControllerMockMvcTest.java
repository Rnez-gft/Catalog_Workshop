package com.gftworkshopcatalog.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshopcatalog.api.dto.controllers.ProductController;
import com.gftworkshopcatalog.model.Product;
import com.gftworkshopcatalog.services.ProductService;

public class ProductControllerMockMvcTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void testAddProduct() throws Exception {

        Product productToAdd = new Product(3L, "New Product", "New Description", 30.0, 300L, "New Category", 0.0, 1.0);

        when(productService.addProduct(any(Product.class))).thenReturn(productToAdd);
     
        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(productToAdd);
        mockMvc.perform(post("/catalog/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(productToAdd.getId()))
                .andExpect(jsonPath("$.name").value(productToAdd.getName()))
		        .andExpect(jsonPath("$.description").value(productToAdd.getDescription()))
		        .andExpect(jsonPath("$.price").value(productToAdd.getPrice()))
		        .andExpect(jsonPath("$.stock").value(productToAdd.getStock()))
		        .andExpect(jsonPath("$.category").value(productToAdd.getCategory()))
		        .andExpect(jsonPath("$.discount").value(productToAdd.getDiscount()))
		        .andExpect(jsonPath("$.weight").value(productToAdd.getWeight()));

        verify(productService).addProduct(any(Product.class));
    }

    
    @Test
    void validateRequiredFieldProductTest() throws Exception {
        Product product = new Product(1L, "Bicicleta", "High-quality mountain bike", 299.99, 15L, "Sports", 0.10,1.0);
        when(productService.addProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/catalog/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bicicleta"));

        verify(productService).addProduct(any(Product.class));
    }
    

    @Test
    void testListAllProducts() throws Exception {
        List<Product> productList = Arrays.asList(new Product(), new Product());
        when(productService.findAllProducts()).thenReturn(productList);


        mockMvc.perform(MockMvcRequestBuilders.get("/catalog/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    
    @Test
    void testGetProductDetails() throws Exception {
        long productId = 1L;
        Product product = new Product();
        when(productService.findProductById(productId)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/catalog/products/{productId}", productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void testDeleteProduct() throws Exception {
        long productId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/catalog/products/{productId}", productId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}