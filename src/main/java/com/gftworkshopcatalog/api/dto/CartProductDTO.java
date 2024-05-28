package com.gftworkshopcatalog.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class CartProductDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private Integer quantity;
    private BigDecimal price;


}