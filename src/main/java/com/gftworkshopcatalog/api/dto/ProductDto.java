package com.gftworkshopcatalog.api.dto;

import com.gftworkshopcatalog.model.CategoryEntity;
import lombok.*;

import java.util.List;

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
    private Integer categoryId;
    private Double weight;
    private Integer currentStock;
    private Integer minStock;
    private List<CategoryDto> category;

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
