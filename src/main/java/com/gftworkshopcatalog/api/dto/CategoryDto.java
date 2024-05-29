package com.gftworkshopcatalog.api.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class CategoryDto {

    private Long categoryId;
    private String name;


    @Override
    public String toString() {
        return "CategoryDto{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }
}