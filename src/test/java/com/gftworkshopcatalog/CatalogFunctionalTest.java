package com.gftworkshopcatalog;

import com.gftworkshopcatalog.model.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
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
                .jsonPath("$.error_code").doesNotExist();
    }

    @Test
    void testGetProductDetails() {
        long productId = 1L; // Ajustar el ID según un producto existente en la base de datos

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
                .jsonPath("$.error_code").doesNotExist(); //Ajustar el valor existente en la base de datos
    }

    @Test
    void testUpdateProduct() {
        long productId = 1L; // Ajustar el ID según un producto existente en la base de datos

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
                .jsonPath("$.error_code").doesNotExist();
    }

    @Test
    void testDeleteProduct() {
         // Ajustar el ID según un producto existente en la base de datos

        webTestClient.delete().uri("/products/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.error_code").doesNotExist();
    }



    @Test
    void testUpdateProductStock() {
        long productId = 1L; // Ajustar el ID según un producto existente en la base de datos
        long newStock = 200; // Nuevo stock a establecer

        webTestClient.patch().uri("/products/{productId}/stock?newStock={newStock}", productId, newStock)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.current_stock").isEqualTo(newStock)
                .jsonPath("$.error_code").doesNotExist();
    }

    @Test
    void testUpdatePriceStock() {
        long productId = 1L; // Ajustar el ID según un producto existente en la base de datos
        long newPrice = 200; // Nuevo stock a establecer

        webTestClient.patch().uri("/products/{productId}/price?newPrice={newPrice}", productId, newPrice)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.price").isEqualTo(newPrice)
                .jsonPath("$.error_code").doesNotExist();
    }


    
}

