package com.gftworkshopcatalog.api.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class CategoryDto {

    private Integer category_Id;
    private String name;
    private List<ProductDto> products;

    @Override
    public String toString() {
        return "CategoryDto{" +
                "categoryId=" + category_Id +
                ", name='" + name + '\'' +
                ", products=" + products +
                '}';
    }
}