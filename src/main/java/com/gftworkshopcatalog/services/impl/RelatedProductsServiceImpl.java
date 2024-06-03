package com.gftworkshopcatalog.services.impl;


import com.gftworkshopcatalog.api.dto.*;
import com.gftworkshopcatalog.services.ProductService;
import com.gftworkshopcatalog.services.RelatedProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
public class RelatedProductsServiceImpl implements RelatedProductsService {


    public String orderUri = "{userId}";

    private RestClient.Builder restClientBuilder;
    private RestClient restClient;

    public RelatedProductsServiceImpl(RestClient.Builder restClient) {
        this.restClient = restClient.build();

    }

    public Optional<OrderDTO> getLatestOrder(Long userId) {
        return Optional.ofNullable(restClient.get()
                .uri(orderUri, userId)
                .retrieve()
                .body(OrderDTO.class));
    }

    public void init() {
        this.restClient = restClientBuilder.build();
    }

}
