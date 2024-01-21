package com.example.number.config;

import com.example.number.dto.NumberDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.logging.Logger;

@Configuration
public class NumberConfiguration {
    private static final Logger LOGGER = Logger.getLogger(NumberConfiguration.class.getName());

    @Value("${number.cacheTTLSecs}")
    private int cacheTTLSecs;
    HttpClient client = HttpClient.create();
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.filter(logRequest()).clientConnector(new ReactorClientHttpConnector(client)).build();
    }

    @Bean
    public Cache<String, NumberDto> caffeineCache() {
        return Caffeine.newBuilder().maximumSize(100)
                .expireAfterWrite(Duration.ofSeconds(cacheTTLSecs)).build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            LOGGER.info("Request: {} {}" + clientRequest.method() + clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> LOGGER.info("{}={}" + name + value)));
            return next.exchange(clientRequest);
        };
    }
}
