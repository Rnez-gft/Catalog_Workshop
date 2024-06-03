package com.gftworkshopcatalog.api.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class OrdersDTO {

    private List<OrderDTO> orderDTOList;

}
