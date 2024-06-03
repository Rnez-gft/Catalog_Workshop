

package com.gftworkshopcatalog.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshopcatalog.api.dto.*;

import com.gftworkshopcatalog.model.ProductEntity;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import com.gftworkshopcatalog.services.impl.RelatedProductsServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RelatedProductsServiceImplTest {


    @Autowired
    WebTestClient webTestClient;
    private static ObjectMapper objectMapper;
    private static MockWebServer mockWebServer;


    @BeforeEach
    void beforeEach() throws IOException {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.start(8081);
    }

    @AfterEach
    void afterEach() throws IOException {
        mockWebServer.close();
    }

    @Test
    void testGetOrders() throws JsonProcessingException {

        OrderProductDTO orderProductDTO = new OrderProductDTO(1L, "Product 1", "Description 1", 100.0, 1);
        OrderedProductsDTO orderedProductsDTO = new OrderedProductsDTO(Collections.singletonList(orderProductDTO));
        OrderDTO orderDTO = new OrderDTO(1L, 1L, 1L, 1L, "Address", "Delivered", "2023-01-01", "2023-01-02", null, null, null, orderedProductsDTO, 100.0);

        mockWebServer.enqueue(new MockResponse()
                        .setBody(objectMapper.writeValueAsString(orderDTO))
                        .addHeader("Content-Type", "application/json"));




        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Related Product 1");
        productEntity.setDescription("Related Product Description 1");
        productEntity.setPrice(150.0);
        productEntity.setCategoryId(1L);


        // When
        webTestClient.get()
                .uri("/RelatedProducts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductEntity.class)
                .value(products -> {
                    // Then
                    assertThat(products).isNotEmpty();
                    assertThat(products.get(0).getName()).isEqualTo("Related Product 1");
                });
    }
}
    /*

        // Mock response data

        List<OrderDTO> mockOrders = Arrays.asList(
                OrderDTO.builder()
                        .id(1L)
                        .cartId(1001L)
                        .userId(1L)
                        .countryId(1L)
                        .fromAddress("123 Main St")
                        .status("PAID")
                        .dateOrdered("2024-05-07 08:00:00")
                        .dateDelivered("2024-05-10 15:00:00")
                        .userDTO(UserDTO.builder()
                                .id(1L)
                                .name("Juan")
                                .lastName("Garca")
                                .email("juangarcia@example.com")
                                .phone("123456789")
                                .build())
                        .addressDTO(AddressDTO.builder()
                                .orderId(1L)
                                .street("Main Street")
                                .number(123)
                                .door("A")
                                .cityName("Springfield")
                                .zipCode("12345")
                                .countryId(1L)
                                .build())
                        .countryDTO(CountryDTO.builder()
                                .id(1L)
                                .name("España")
                                .tax(21)
                                .prefix("+34")
                                .timeZone("Europe/Madrid")
                                .build())
                        .orderedProductsDTO(OrderedProductsDTO.builder()
                                .orderProductDTOList(Arrays.asList(
                                        OrderProductDTO.builder()
                                                .productId(1L)
                                                .name("Product1")
                                                .description("Description1")
                                                .price(50.0)
                                                .quantity(2)
                                                .build(),
                                        OrderProductDTO.builder()
                                                .productId(2L)
                                                .name("Product2")
                                                .description("Description2")
                                                .price(30.0)
                                                .quantity(1)
                                                .build(),
                                        OrderProductDTO.builder()
                                                .productId(3L)
                                                .name("Product3")
                                                .description("Description3")
                                                .price(40.0)
                                                .quantity(3)
                                                .build()
                                ))
                                .build())
                        .totalPrice(18.0)
                        .build(),
                OrderDTO.builder()
                        .id(4L)
                        .cartId(1004L)
                        .userId(1L)
                        .countryId(1L)
                        .fromAddress("101 Maple Ave")
                        .status("DELIVERED")
                        .dateOrdered("2024-05-10 11:00:00")
                        .dateDelivered("2024-05-12 18:00:00")
                        .userDTO(UserDTO.builder()
                                .id(1L)
                                .name("Juan")
                                .lastName("Garca")
                                .email("juangarcia@example.com")
                                .phone("123456789")
                                .build())
                        .addressDTO(AddressDTO.builder()
                                .orderId(4L)
                                .street("Oak Street")
                                .number(789)
                                .door("C")
                                .cityName("Capital City")
                                .zipCode("10112")
                                .countryId(3L)
                                .build())
                        .countryDTO(CountryDTO.builder()
                                .id(1L)
                                .name("España")
                                .tax(21)
                                .prefix("+34")
                                .timeZone("Europe/Madrid")
                                .build())
                        .orderedProductsDTO(OrderedProductsDTO.builder()
                                .orderProductDTOList(Arrays.asList(
                                        OrderProductDTO.builder()
                                                .productId(1006L)
                                                .name("Product6")
                                                .description("Description6")
                                                .price(80.0)
                                                .quantity(4)
                                                .build(),
                                        OrderProductDTO.builder()
                                                .productId(1007L)
                                                .name("Product7")
                                                .description("Description7")
                                                .price(90.0)
                                                .quantity(2)
                                                .build()
                                ))
                                .build())
                        .totalPrice(17.0)
                        .build());
// When
        Long userId = 1L;
        webTestClient.get()
                .uri("/catalog/RelatedProducts/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockOrders)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDTO.class)
                .value(userDto -> {
                    // Then
                    assertThat(passwordEncoder.matches(newUser.getPassword(), userDto.getPassword())).isTrue();
                    assertThat(userDto.getName()).isEqualTo(newUser.getName());
                    assertThat(userDto.getLastName()).isEqualTo(newUser.getLastName());
                    assertThat(userDto.getEmail()).isEqualTo(newUser.getEmail());
                    assertThat(userDto.getBirthDate()).isEqualTo(newUser.getBirthDate());
                    assertThat(userDto.getFidelityPoints()).isEqualTo(newUser.getFidelityPoints());
                    assertThat(userDto.getPhone()).isEqualTo(newUser.getPhone());

                    assertThat(userDto.getAddress().getCityName()).isEqualTo(newUser.getAddress().getCityName());
                    assertThat(userDto.getAddress().getZipCode()).isEqualTo(newUser.getAddress().getZipCode());
                    assertThat(userDto.getAddress().getStreet()).isEqualTo(newUser.getAddress().getStreet());
                    assertThat(userDto.getAddress().getNumber()).isEqualTo(newUser.getAddress().getNumber());
                    assertThat(userDto.getAddress().getDoor()).isEqualTo(newUser.getAddress().getDoor());

                    assertThat(userDto.getCountry().getName()).isEqualTo(newUser.getCountry().getName());
                    assertThat(userDto.getCountry().getTax()).isEqualTo(newUser.getCountry().getTax());
                    assertThat(userDto.getCountry().getPrefix()).isEqualTo(newUser.getCountry().getPrefix());
                    assertThat(userDto.getCountry().getTimeZone()).isEqualTo(newUser.getCountry().getTimeZone());
                });
    }

    @Test
    void getLatestOrder_Success() {
        Long userId = 1L;
        OrdersDTO mockOrder = new OrdersDTO();
        when(restClientBuilder.build()).thenReturn(mockRestClient);
        when(mockRestClient.get()
                .uri(relatedProductsService.orderUri, userId)
                .retrieve()
                .body(OrdersDTO.class))
                .thenReturn(mockOrder);

        Optional<OrderDTO> orderDTO = relatedProductsService.getLatestOrder(userId);

        assertTrue(orderDTO.isPresent());
        assertEquals(mockOrder, orderDTO.get());
    }

    @Test
    void getLatestOrder_NotFound() {
        Long userId = 1L;
        when(restClientBuilder.build()).thenReturn(mockRestClient);
        when(mockRestClient.get().thenReturn()
                .uri(relatedProductsService.orderUri, userId)
                .retrieve()
                .body(OrderDTO.class))
                .thenThrow(new RestClientResponseException("Order not found", 404, "Not Found", null, null, null));

        Optional<OrderDTO> orderDTO = relatedProductsService.getLatestOrder(userId);

        assertFalse(orderDTO.isPresent());
    }
}
*/
