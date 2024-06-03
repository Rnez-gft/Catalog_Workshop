package com.gftworkshopcatalog.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CountryDTO {

    private Long id;
    private String name;
    private Integer tax;
    private String prefix;
    private String timeZone;

}
