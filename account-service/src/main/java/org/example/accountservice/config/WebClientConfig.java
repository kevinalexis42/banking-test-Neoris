package org.example.accountservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {
    
    @Value("${customer.service.url:http://localhost:8080}")
    private String customerServiceUrl;
    
    @Bean
    public WebClient customerServiceWebClient() {
        log.info("Configuring WebClient for Customer Service with URL: {}", customerServiceUrl);
        return WebClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }
}

