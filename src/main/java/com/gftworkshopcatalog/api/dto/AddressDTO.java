package com.gftworkshopcatalog.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class AddressDTO {

    private Long orderId;
    private String street;
    private Integer number;
    private String door;
    private String cityName;
    private String zipCode;
    private Long countryId;


}
