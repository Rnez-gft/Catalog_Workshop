package com.gftworkshopcatalog;


import com.gftworkshopcatalog.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gftworkshopcatalog.model.ProductEntity;
import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.repositories.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDate;


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
    @DisplayName("Find all products - Success")
    void testListAllProducts() {
        webTestClient.get().uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductEntity.class).hasSize(40)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertFalse(response.getResponseBody().isEmpty());
                });

    }
    @Test
    @DisplayName("Add NewProduct - Success")

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
    @DisplayName("Get Product by ID - Success")
    void testGetProductDetails() {
        long productId = 5L;

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

    @DisplayName("Add new product - BadRequest")
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
    @DisplayName("Update Product - Success")
    void testUpdateProduct() {
        long productId = 5L;

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
    @DisplayName("Update Product - Not Found")
    void testUpdateProduct_NotFound() {
        long nonExistingProductId = 999L;

        ProductEntity updatedProductEntity = new ProductEntity();
        updatedProductEntity.setName("Updated Product Name");
        updatedProductEntity.setDescription("Updated Product Description");
        updatedProductEntity.setPrice(29.99);
        updatedProductEntity.setCategoryId(6L);
        updatedProductEntity.setWeight(2.5);
        updatedProductEntity.setCurrentStock(150);
        updatedProductEntity.setMinStock(15);

        webTestClient.put().uri("/products/{id}", nonExistingProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedProductEntity)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("Update Product with Invalid Data - Bad Request")
    void testUpdateProduct_InvalidData() {
        long productId = 1L;

        ProductEntity invalidProductEntity = new ProductEntity();
        invalidProductEntity.setPrice(-19.99);

        webTestClient.put().uri("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidProductEntity)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
    }


    @Test
    @DisplayName("Delete Product - Success")
    void testDeleteProduct() {

        webTestClient.delete().uri("/products/{id}", 3L)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.errorCode").doesNotExist();
    }

    @Test
    @DisplayName("Delete Product - Not Found")
    void testDeleteProduct_NotFound() {
        long nonExistingProductId = 999L;

        webTestClient.delete().uri("/products/{id}", nonExistingProductId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
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
                    assertFalse(response.getResponseBody().isEmpty());
                });
    }
    @Test
    @DisplayName("Get Promotion Details - Success")
    void testGetPromotionDetailsSuccess() {
        PromotionEntity existingPromotion = promotionRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No promotions available for testing"));

        webTestClient.get().uri("/promotions/{id}", existingPromotion.getPromotionId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.promotionId").isEqualTo(existingPromotion.getPromotionId());
    }

    @Test
    @DisplayName("Get Promotion Details - Not Found")
    void testGetPromotionDetails_NotFound() {
        long nonExistingPromotionId = 999L;

        webTestClient.get().uri("/promotions/{id}", nonExistingPromotionId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
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
    @DisplayName("Add New Promotion with Invalid Data - Bad Request")
    void testAddNewPromotion_InvalidData() {
        PromotionEntity invalidPromotion = new PromotionEntity();
        // Do not set required fields

        webTestClient.post().uri("/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidPromotion)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
    }

    @Test
    @DisplayName("Update a promotion - Success")
    void testUpdatePromotionSuccess() {
        long promotionId = 1L;
        PromotionEntity updatedPromotionDetails = new PromotionEntity(promotionId, 1L, 0.20, "SEASONAL", 10, LocalDate.now(), LocalDate.now().plusDays(30), true);

        webTestClient.put().uri("/promotions/{id}", promotionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPromotionDetails)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.promotionId").isEqualTo(promotionId)
                .jsonPath("$.discount").isEqualTo(0.20);
    }

    @Test
    @DisplayName("Update Promotion - Not Found")
    void testUpdatePromotion_NotFound() {
        long nonExistingPromotionId = 999L;
        PromotionEntity updatedPromotionDetails = new PromotionEntity(nonExistingPromotionId, 1L, 0.20, "SEASONAL", 10, LocalDate.now(), LocalDate.now().plusDays(30), true);

        webTestClient.put().uri("/promotions/{id}", nonExistingPromotionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPromotionDetails)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("Update Promotion with Invalid Data - Bad Request")
    void testUpdatePromotion_InvalidData() {
        long promotionId = 1L;
        PromotionEntity invalidPromotionDetails = new PromotionEntity();
        // Do not set required fields or set invalid values

        webTestClient.put().uri("/promotions/{id}", promotionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidPromotionDetails)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
    }

    @Test
    @DisplayName("Delete a promotion - Success")
    void testDeletePromotionSuccess() {
        Long promotionId = 3L;

        webTestClient.delete().uri("/promotions/{id}", promotionId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Delete Promotion - Not Found")
    void testDeletePromotion_NotFound() {
        long nonExistingPromotionId = 999L;

        webTestClient.delete().uri("/promotions/{id}", nonExistingPromotionId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("Find all categories - Success")
    void testFindAllCategories() {

        webTestClient.get().uri("/categories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CategoryEntity.class).hasSize(7)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertFalse(response.getResponseBody().isEmpty());
                });
    }

    @Test
    @DisplayName("Add NewProduct - Success")

    void testAddNewCategory() {
        CategoryEntity newCategoryEntity = new CategoryEntity();
        newCategoryEntity.setCategoryId(7L);
        newCategoryEntity.setName("Category7");

        webTestClient.post().uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newCategoryEntity)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.categoryId").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Category7");
    }

    @Test
    @DisplayName("Delete Category by Id - Success")
    void testdeleteCategoryById() {
        long categoryId = 7L;

        CategoryEntity newCategoryEntity = new CategoryEntity();
        newCategoryEntity.setCategoryId(7L);
        newCategoryEntity.setName("Category7");

        webTestClient.post().uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newCategoryEntity)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.categoryId").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Category7");

        webTestClient.delete().uri("/categories/{categoryId}", categoryId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.errorCode").doesNotExist();
    }

    @Test
    @DisplayName("Get list of Product by categoryId - Success")
    void testlistProductsByCategoryId() {
        long categoryId = 1L;

        webTestClient.get().uri("/categories/{categoryId}/products", categoryId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].categoryId").isEqualTo(categoryId)
                .jsonPath("$[1].categoryId").isEqualTo(categoryId)
                .jsonPath("$[2].categoryId").isEqualTo(categoryId);


    }

    @Test
    @DisplayName("List Products by Category ID and Name - Success")
    void testListProductsByCategoryIdAndName_Success() {
        long categoryId = 2L;
        String name = "pu";

        webTestClient.get()
                .uri("/categories/{categoryId}/{name}/products", categoryId, name)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductEntity.class);

    }

    @Test
    @DisplayName("Add New Category with Invalid Data - Bad Request")
    void testAddNewCategory_InvalidData() {
        CategoryEntity newCategoryEntity = new CategoryEntity();

        webTestClient.post().uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newCategoryEntity)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BAD_REQUEST");
    }

    @Test
    @DisplayName("Delete Category By Id - Not Found")
    void testDeleteCategoryById_NotFound() {
        long nonExistingCategoryId = 999L;

        webTestClient.delete().uri("/categories/{categoryId}", nonExistingCategoryId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("Get list of Products by CategoryId - Not Found")
    void testListProductsByCategoryId_NotFound() {
        long nonExistingCategoryId = 999L;

        webTestClient.get().uri("/categories/{categoryId}/products", nonExistingCategoryId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }

    @Test
    @DisplayName("List Products By Category ID and Name - Not Found")
    void testListProductsByCategoryIdAndName_NotFound() {
        long nonExistingCategoryId = 2L;
        String name = "nonexisting";

        webTestClient.get()
                .uri("/categories/{categoryId}/{name}/products", nonExistingCategoryId, name)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("NOT_FOUND");
    }

}

