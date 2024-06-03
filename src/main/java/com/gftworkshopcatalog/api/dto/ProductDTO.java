package com.gftworkshopcatalog.api.dto;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long categoryId;
    private Double weight;
    private Integer currentStock;
    private Integer minStock;

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category_Id=" + categoryId +
                ", weight=" + weight +
                ", current_stock=" + currentStock +
                ", min_stock=" + minStock +
                '}';
    }
}
