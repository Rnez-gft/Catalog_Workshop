package com.gftworkshopcatalog.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer category_Id;
    private Double weight;
    private Integer current_stock;
    private Integer min_stock;

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category_Id=" + category_Id +
                ", weight=" + weight +
                ", current_stock=" + current_stock +
                ", min_stock=" + min_stock +
                '}';
    }
}
