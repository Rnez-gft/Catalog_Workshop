package com.gftworkshopcatalog.services;

import com.gftworkshopcatalog.api.dto.OrderDTO;
import com.gftworkshopcatalog.model.ProductEntity;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


public interface RelatedProductsService {


    Optional<OrderDTO> getLatestOrder(Long userId);

}
