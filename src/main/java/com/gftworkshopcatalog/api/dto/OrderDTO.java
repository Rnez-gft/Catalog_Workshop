package com.gftworkshopcatalog.api.dto;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private Long cartId;
    private Long userId;
    private Long countryId;
    private String fromAddress;
    private String status;
    private String dateOrdered;
    private String dateDelivered;
    private UserDTO userDTO;
    private AddressDTO addressDTO;
    private CountryDTO countryDTO;
    private OrderedProductsDTO orderedProductsDTO;
    private Double totalPrice;

}
