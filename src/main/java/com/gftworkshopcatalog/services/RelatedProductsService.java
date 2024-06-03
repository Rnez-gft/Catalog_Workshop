package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.api.dto.OrdersDTO;

import java.util.Optional;


public interface RelatedProductsService {


    Optional<OrdersDTO> getLatestOrder(Long userId);

}
