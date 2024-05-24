package com.gftworkshopcatalog.api.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class CategoryDto {

    private Long category_Id;
    private String name;


    @Override
    public String toString() {
        return "CategoryDto{" +
                "categoryId=" + category_Id +
                ", name='" + name + '\'' +
                '}';
    }
}