package com.gftworkshopcatalog;

import com.gftworkshopcatalog.model.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogFunctionalTest {

    @Autowired
    private WebTestClient webTestClient;
/*
    @LocalServerPort
    private int port;

  */


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
                .jsonPath("$.[0].category_Id").isNumber()
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
        newProductEntity.setCategory_Id(6);
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
                .jsonPath("$.category_Id").isEqualTo(6)
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
                .jsonPath("$.category_Id").isNumber()
                .jsonPath("$.weight").isNumber()
                .jsonPath("$.current_stock").isNumber()
                .jsonPath("$.min_stock").isNumber()
                .jsonPath("$.errorCode").doesNotExist(); //Ajustar el valor existente en la base de datos
    }

    @Test
    @DisplayName("Test UpdateProduct()")
    void testUpdateProduct() {
        long productId = 1L;

        ProductEntity updatedProductEntity = new ProductEntity();
        updatedProductEntity.setName("Updated Product Name");
        updatedProductEntity.setDescription("Updated Product Description");
        updatedProductEntity.setPrice(29.99);
        updatedProductEntity.setCategory_Id(7);
        updatedProductEntity.setWeight(2.5);
        updatedProductEntity.setCurrent_stock(150);
        updatedProductEntity.setMin_stock(15);

        webTestClient.put().uri("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedProductEntity)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.name").isEqualTo("Updated Product Name")
                .jsonPath("$.description").isEqualTo("Updated Product Description")
                .jsonPath("$.price").isEqualTo(29.99)
                .jsonPath("$.category_Id").isEqualTo(7)
                .jsonPath("$.weight").isEqualTo(2.5)
                .jsonPath("$.current_stock").isEqualTo(150)
                .jsonPath("$.min_stock").isEqualTo(15)
                .jsonPath("$.errorCode").doesNotExist();
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
    @DisplayName("Test UpdateProductStock()")
    void testUpdateProductStock() {
        long productId = 1L;
        long newStock = 200;

        webTestClient.patch().uri("/products/{productId}/stock?newStock={newStock}", productId, newStock)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.current_stock").isEqualTo(newStock)
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

        webTestClient.get().uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product not found");

        long newStock = 200;

        webTestClient.patch().uri("/products/{productId}/stock?newStock={newStock}", productId, newStock)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product not found");

        long newPrice = 200;

        webTestClient.patch().uri("/products/{productId}/price?newPrice={newPrice}", productId, newPrice)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product not found");

    }

    @Test
    @DisplayName("Test BadRequestError()")
    void testBadRequestError() {

        ProductEntity newProductEntity = new ProductEntity();

        newProductEntity.setDescription("Test Description");
        newProductEntity.setPrice(19.99);
        newProductEntity.setCategory_Id(1);
        newProductEntity.setWeight(15.00);
        newProductEntity.setCurrent_stock(100);
        newProductEntity.setMin_stock(10);

        webTestClient.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProductEntity)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Bad request");
    }

    @Test
    @DisplayName("Test InternalServerError()")
    void testInternalServerError() {

        long productId = -1L;

        webTestClient.get().uri("/products/{id}", productId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("Internal server error");

    }

}