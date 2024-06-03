package com.gftworkshopcatalog.services.impl;


import com.gftworkshopcatalog.api.dto.*;
import com.gftworkshopcatalog.services.RelatedProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.Optional;

@Slf4j
@Service
public class RelatedProductsServiceImpl implements RelatedProductsService {



    public String baseUrl;
    public String orderUri;
    private final RestClient restClient;

    public RelatedProductsServiceImpl(RestClient restClient,
                           @Value("${orders.api.base-url}") String baseUrl,
                           @Value("${orders.api.order-uri}") String orderUri) {
        this.baseUrl = baseUrl;
        this.orderUri = orderUri;
        this.restClient = restClient;
    }


    public Optional<OrdersDTO> getLatestOrder(Long userId){

        return Optional.ofNullable(restClient.get()
                .uri(baseUrl + orderUri + userId)
                .retrieve()
                .body(OrdersDTO.class));
    }



}
