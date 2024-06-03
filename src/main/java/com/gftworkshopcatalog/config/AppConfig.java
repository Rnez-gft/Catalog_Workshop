package com.gftworkshopcatalog.config;


import lombok.Generated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
@Generated
public class AppConfig {
    @Value("${orders.url}")
    private String urlOrders;
    @Bean
    public RestClient.Builder restClientBuilder(){
        return RestClient.builder().baseUrl(urlOrders);
    }

}
