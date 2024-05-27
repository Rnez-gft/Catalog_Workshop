package com.gftworkshopcatalog;

import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CatalogFunctionalTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("Find all products")
    void testListAllProducts() {
        webTestClient.get().uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductEntity.class).hasSize(20)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertFalse(response.getResponseBody().isEmpty());
                });
    }
    @Test
    @DisplayName("Add NewProduct")
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
    @DisplayName("Get Product by ID")
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
                .jsonPath("$.errorCode").doesNotExist();

    }

    @Test
    @DisplayName("Add new product with invalid data")
    void testAddInvalidProduct() {
        ProductEntity invalidProduct = new ProductEntity();
        invalidProduct.setPrice(-19.99); // Invalid price

        webTestClient.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidProduct)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo("INTERNAL_SERVER_ERROR");
    }
    @Test
    @DisplayName("Test UpdateProduct()")
    void testUpdateProduct() {
        long productId = 1L;

        ProductEntity updatedProductEntity = new ProductEntity();
        updatedProductEntity.setName("Updated Product Name");
        updatedProductEntity.setDescription("Updated Product Description");
        updatedProductEntity.setPrice(29.99);
        updatedProductEntity.setCategoryId(6L);
        updatedProductEntity.setWeight(2.5);
        updatedProductEntity.setCurrentStock(150);
        updatedProductEntity.setMinStock(15);

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
                .jsonPath("$.categoryId").isEqualTo(6L)
                .jsonPath("$.weight").isEqualTo(2.5)
                .jsonPath("$.currentStock").isEqualTo(150)
                .jsonPath("$.minStock").isEqualTo(15)
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
    @DisplayName("Update non-existent product price")
    void testUpdateNonExistentProductPrice() {
        long productId = 999L; // Non-existent product ID
        double newPrice = 200.0;

        Map<String, Object> priceUpdate = Map.of("newPrice", newPrice);

        webTestClient.patch().uri("/products/{productId}/price", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(priceUpdate)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500);
    }

}

