package com.gftworkshopcatalog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long stock;
    private String category;
    private Double discount;
    private Double weight;

    @Override
    public String toString() {
        return "ProductDTO [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price +
                ", stock=" + stock + ", category=" + category + ", discount=" + discount + ", weight=" + weight + "]";
    }
}
