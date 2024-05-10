package com.gftworkshopcatalog.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.gftworkshopcatalog.api.dto.ProductDto;

public class ProductDtoTest {

    @Test
    public void testConstructorAndGetters() {
        ProductDto product = new ProductDto(1L, "Bike", "Mountain bike", 299.99, 5L, "Sports", 0.10, 15.0);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Bike");
        assertThat(product.getDescription()).isEqualTo("Mountain bike");
        assertThat(product.getPrice()).isEqualTo(299.99);
        assertThat(product.getStock()).isEqualTo(5L);
        assertThat(product.getCategory()).isEqualTo("Sports");
        assertThat(product.getDiscount()).isEqualTo(0.10);
        assertThat(product.getWeight()).isEqualTo(15.0);
    }

    @Test
    public void testSetters() {
        ProductDto product = new ProductDto();
        product.setId(1L);
        product.setName("Bike");
        product.setDescription("Mountain bike");
        product.setPrice(299.99);
        product.setStock(5L);
        product.setCategory("Sports");
        product.setDiscount(0.10);
        product.setWeight(15.0);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Bike");
        assertThat(product.getDescription()).isEqualTo("Mountain bike");
        assertThat(product.getPrice()).isEqualTo(299.99);
        assertThat(product.getStock()).isEqualTo(5L);
        assertThat(product.getCategory()).isEqualTo("Sports");
        assertThat(product.getDiscount()).isEqualTo(0.10);
        assertThat(product.getWeight()).isEqualTo(15.0);
    }

    @Test
    public void testToString() {
        ProductDto product = new ProductDto(1L, "Bike", "Mountain bike", 299.99, 5L, "Sports", 0.10, 15.0);
        String expectedString = "ProductDTO [id=1, name=Bike, description=Mountain bike, price=299.99, stock=5, category=Sports, discount=0.1, weight=15.0]";
        assertThat(product.toString()).isEqualTo(expectedString);
    }
}
