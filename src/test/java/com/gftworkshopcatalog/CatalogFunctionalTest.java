package com.gftworkshopcatalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.ProductRepository;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CatalogFunctionalTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    }
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
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
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
    @DisplayName("List all promotions - Success")
    void testGetAllPromotionsSuccess() {
        webTestClient.get().uri("/promotions")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(PromotionEntity.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertTrue(response.getResponseBody().size() > 0); // Check that some promotions are returned
                });
    }
    @Test
    @DisplayName("Get Promotion Details - Success")
    void testGetPromotionDetailsSuccess() {
        PromotionEntity existingPromotion = promotionRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No promotions available for testing"));

        webTestClient.get().uri("/promotions/{id}", existingPromotion.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(existingPromotion.getId());
    }

    @Test
    @DisplayName("Add a new promotion - Success")
    void testAddNewPromotionSuccess() throws JsonProcessingException {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = startDate.plusDays(10);

        PromotionEntity newPromotion = new PromotionEntity(1L, 1L, 10.0, "Volume", 5, startDate, endDate, true);

        String promotionJson = objectMapper.writeValueAsString(newPromotion);

        webTestClient.post().uri("/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(promotionJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.categoryId").isEqualTo(newPromotion.getCategoryId())
                .jsonPath("$.discount").isEqualTo(newPromotion.getDiscount())
                .jsonPath("$.promotionType").isEqualTo(newPromotion.getPromotionType())
                .jsonPath("$.volumeThreshold").isEqualTo(newPromotion.getVolumeThreshold())
                .jsonPath("$.startDate").isEqualTo(startDate.toString())
                .jsonPath("$.endDate").isEqualTo(endDate.toString())
                .jsonPath("$.isActive").isEqualTo(newPromotion.getIsActive());
    }
    @Test
    @DisplayName("Update a promotion - Success")
    void testUpdatePromotionSuccess() {
        long promotionId = 1L; // Assumed existing promotion ID
        PromotionEntity updatedPromotionDetails = new PromotionEntity(promotionId, 1L, 0.20, "SEASONAL", 10, LocalDate.now(), LocalDate.now().plusDays(30), true);

        webTestClient.put().uri("/promotions/{id}", promotionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPromotionDetails)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(promotionId)
                .jsonPath("$.discount").isEqualTo(0.20);
    }

    @Test
    @DisplayName("Delete a promotion - Success")
    void testDeletePromotionSuccess() {
        Long promotionId = 2L;

        webTestClient.delete().uri("/promotions/{id}", promotionId)
                .exchange()
                .expectStatus().isNoContent();
    }

}

