package com.gftworkshopcatalog.api.dto;


import lombok.*;
import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class OrderedProductsDTO {

    private List<OrderProductDTO> orderProductDTOList;


}
