package com.gftworkshopcatalog;

import com.gftworkshopcatalog.model.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogFunctionalTest {

    private WebTestClient webTestClient;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/catalog").build();
    }

    @Test
    @DisplayName("Find all products")
    void testListAllProducts() {
        webTestClient.get().uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].id").isNotEmpty()
                .jsonPath("$.[0].name").isNotEmpty()
                .jsonPath("$.[0].description").isNotEmpty()
                .jsonPath("$.[0].price").isNumber()
                .jsonPath("$.[0].categoryId").isNumber()
                .jsonPath("$.[0].weight").isNumber()
                .jsonPath("$.[0].currentStock").isNumber()
                .jsonPath("$.[0].minStock").isNumber()
                .jsonPath("$.[0].error_code").doesNotExist();
    }
    @Test
    @DisplayName("Add new product")
    void testAddNewProduct() {
        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setName("Test Product");
        newProductEntity.setDescription("Test Description");
        newProductEntity.setPrice(19.99);
        newProductEntity.setCategoryId(6L);
        newProductEntity.setWeight(2.0);
        newProductEntity.setCurrentStock(100);
        newProductEntity.setMinStock(10);

        webTestClient.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProductEntity)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.description").isEqualTo("Test Description")
                .jsonPath("$.price").isEqualTo(19.99)
                .jsonPath("$.categoryId").isEqualTo(6)
                .jsonPath("$.weight").isEqualTo(2.0)
                .jsonPath("$.currentStock").isEqualTo(100)
                .jsonPath("$.minStock").isEqualTo(10)
                .jsonPath("$.errorCode").doesNotExist();
    }
    @Test
    @DisplayName("Find product by ID")
    void testGetProductDetails() {
        long productId = 1L;

        webTestClient.get().uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.description").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.categoryId").isNumber()
                .jsonPath("$.weight").isNumber()
                .jsonPath("$.currentStock").isNumber()
                .jsonPath("$.minStock").isNumber()
                .jsonPath("$.errorCode").doesNotExist(); //Ajustar el valor existente en la base de datos
    }
    @Test
    @DisplayName("Delete Product")
    void testDeleteProduct() {
        webTestClient.delete().uri("/products/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.errorCode").doesNotExist();
    }
    @Test
    @DisplayName("Update Product Price")
    void testUpdateProductPrice() {
        long productId = 1L;
        long newPrice = 200;

        webTestClient.patch().uri("/products/{productId}/price?newPrice={newPrice}", productId, newPrice)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.price").isEqualTo(newPrice)
                .jsonPath("$.errorCode").doesNotExist();
    }
    @Test
    @DisplayName("Product Not Found Error")
    void testProductNotFoundError() {
        long productId = 999L;
        int newStock = 200;

        webTestClient.patch().uri("/products/{productId}/stock?newStock={newStock}", productId, newStock)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Product not found with ID: " + productId);

    }
}