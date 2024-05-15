package com.gftworkshopcatalog;

import com.gftworkshopcatalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CatalogFunctionalTest {
	
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
        Product newProduct = new Product();
        newProduct.setName("Test Product");
        newProduct.setDescription("Test Description");
        newProduct.setPrice(19.99);
        newProduct.setCategory_Id(6);
        newProduct.setWeight(2.0);
        newProduct.setCurrent_stock(100);
        newProduct.setMin_stock(10);

        webTestClient.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
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

        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product Name");
        updatedProduct.setDescription("Updated Product Description");
        updatedProduct.setPrice(29.99);
        updatedProduct.setCategory_Id(7);
        updatedProduct.setWeight(2.5);
        updatedProduct.setCurrent_stock(150);
        updatedProduct.setMin_stock(15);

        webTestClient.put().uri("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedProduct)
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
                .jsonPath("$.errorCode").doesNotExist();
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
                .jsonPath("$.errorCode").doesNotExist();
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
                .jsonPath("$.errorCode").doesNotExist();
    }

    @Test
    void testGetRelatedProducts() {
        long productId = 1L; // Ajustar el ID según un producto existente en la base de datos

        webTestClient.get().uri("/products/{id}/recommendations", productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isNotEmpty()
                .jsonPath("$[0].name").isNotEmpty()
                .jsonPath("$[0].description").isNotEmpty()
                .jsonPath("$[0].price").isNumber()
                .jsonPath("$[0].category_Id").isNumber()
                .jsonPath("$[0].weight").isNumber()
                .jsonPath("$[0].current_stock").isNumber()
                .jsonPath("$[0].min_stock").isNumber()
                .jsonPath("$.errorCode").doesNotExist();
    }
    @Test
    void testProductNotFoundError() {
        // Simulamos un producto que no existe en la base de datos
        long productId = 999L;

        webTestClient.get().uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product not found");

    }

    @Test
    void testBadRequestError() {
        // Simulamos una solicitud incorrecta que cause un error 400
        Product newProduct = new Product();
        // No proporcionamos el nombre del producto, lo que debería generar un error de solicitud incorrecta
        newProduct.setDescription("Test Description");
        newProduct.setPrice(19.99);
        newProduct.setCurrent_stock(100);

        webTestClient.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Bad request, missing required fields");
    }

    @Test
    void testUnauthorizedError() {
        // Simulamos una solicitud que requiere autenticación pero no se proporcionan credenciales válidas
        webTestClient.get().uri("/products")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("UNAUTHORIZED")
                .jsonPath("$.msg").isEqualTo("Unauthorized access, please provide valid credentials");
    }

    @Test
    void testForbiddenError() {
        // Simulamos una solicitud que no tiene permiso para acceder a un recurso
        long productId = 123L; // Supongamos que este ID de producto está protegido y el usuario no tiene permiso para acceder

        webTestClient.get().uri("/products/{id}", productId)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("FORBIDDEN")
                .jsonPath("$.msg").isEqualTo("Forbidden, you don't have permission to access this resource");
    }

    @Test
    void testInternalServerError() {
        // Simular una solicitud que cause un error interno del servidor
        long productId = 0L; // Este ID de producto provocará un error interno en el servidor

        webTestClient.get().uri("/products/{id}", productId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("Internal server error occurred");
    }

    
}

