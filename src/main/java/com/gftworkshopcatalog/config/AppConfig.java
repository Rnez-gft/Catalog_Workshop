package com.gftworkshopcatalog.config;


import lombok.Generated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
@Generated
public class AppConfig {
    @Bean
    public RestClient restClient(){
        return RestClient.create();
    }

}
