package com.gftworkshopcatalog;

import com.gftworkshopcatalog.model.ProductEntity;
import lombok.Generated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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
    @DisplayName("Test ListAllProducts()")
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
                .jsonPath("$.[0].current_stock").isNumber()
                .jsonPath("$.[0].min_stock").isNumber()
                .jsonPath("$.[0].error_code").doesNotExist();
    }

    @Test
    @DisplayName("Test AddNewProduct()")
    void testAddNewProduct() {
        ProductEntity newProductEntity = new ProductEntity();
        newProductEntity.setName("Test Product");
        newProductEntity.setDescription("Test Description");
        newProductEntity.setPrice(19.99);
        newProductEntity.setCategoryId(6L);
        newProductEntity.setWeight(2.0);
        newProductEntity.setCurrent_stock(100);
        newProductEntity.setMin_stock(10);

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
                .jsonPath("$.current_stock").isEqualTo(100)
                .jsonPath("$.min_stock").isEqualTo(10)
                .jsonPath("$.errorCode").doesNotExist();
    }

    @Test
    @DisplayName("Test GetProductDetails()")
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
                .jsonPath("$.current_stock").isNumber()
                .jsonPath("$.min_stock").isNumber()
                .jsonPath("$.errorCode").doesNotExist(); //Ajustar el valor existente en la base de datos
    }


    @Test
    @DisplayName("Test DeleteProduct()")
    void testDeleteProduct() {

        webTestClient.delete().uri("/products/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.errorCode").doesNotExist();
    }


    @Test
    @DisplayName("Test UpdateProductPrice()")
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
    @DisplayName("Test ProductNotFoundError()")
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