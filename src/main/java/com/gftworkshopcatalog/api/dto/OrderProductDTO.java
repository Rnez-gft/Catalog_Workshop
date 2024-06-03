package com.gftworkshopcatalog.api.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class OrderProductDTO {

    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;

}
