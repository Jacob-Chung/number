package com.example.number.service;

import com.example.number.dto.NumberDto;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.TreeSet;

@Service
public class NumberServiceImpl implements NumberService {
    private final WebClient webClient;
    private Cache<String, NumberDto> caffeineCache;
    @Value("${number.timeoutMillis}")
    private int timeoutMillis;



    public NumberServiceImpl(WebClient webClient, Cache<String, NumberDto> caffeineCache) {
        this.webClient = webClient;
        this.caffeineCache = caffeineCache;
    }


    @Override
    public Mono<NumberDto> getNumber(List<String> urlList) {
        Flux<String> uriFlux = Flux.fromIterable(urlList);

        /* Here we use flatMap to send the request to each url in parallel
         and then reduce the result into one NumberDto.
         The timeout is set to 500ms */
        return uriFlux.flatMap(this::sendHttpRequestToUrl)
                .reduce(new NumberDto(new TreeSet<Integer>()), (resp1, resp2) -> {
                    resp1.getNumbers().addAll(resp2.getNumbers());
                    return resp1;
                });

    }

    private Mono<NumberDto> sendHttpRequestToUrl(String url) {
        NumberDto cachedVersion = this.caffeineCache.getIfPresent(url);
        if (cachedVersion != null) {
            return Mono.just(cachedVersion);
        } else {
            Mono<NumberDto> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(NumberDto.class)
                    .doOnNext(response -> {
                        // This will be executed when the actual response is received
                        // Then store the response in a cache
                        this.caffeineCache.put(url, response);
                    });

            // set the timeout and return an empty NumberDto if the timeout occurs
            return responseMono.timeout(Duration.ofMillis(timeoutMillis)).onErrorResume(e -> Mono.just(new NumberDto(new TreeSet<Integer>())));

        }

    }



}
