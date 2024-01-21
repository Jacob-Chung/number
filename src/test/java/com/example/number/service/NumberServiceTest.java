package com.example.number.service;


import com.example.number.dto.NumberDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class NumberServiceTest {
    public static MockWebServer mockBackEnd;

    private NumberServiceImpl numberService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = mockBackEnd.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        numberService = new NumberServiceImpl(webClient, Caffeine.newBuilder().maximumSize(100)
                .expireAfterWrite(Duration.ofSeconds(1)).build());
        // we don't want the timeout happening too frequently during the test so we set it to 5 seconds
        ReflectionTestUtils.setField(numberService, "timeoutMillis", 5000);
    }

    @Test
    void getNumberTest_success_no_cache() {
        // Mock response of Rand URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[44,99,83,76,3,36,64,15,67,28],\"strings\":[\"forty-four\",\"ninety-nine\",\"eighty-three\",\"seventy-six\",\"three\",\"thirty-six\",\"sixty-four\",\"fifteen\",\"sixty-seven\",\"twenty-eight\"]}")
                .setHeader("Content-Type", "application/json"));
        // Mock response of Primes URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97],\"strings\":[\"two\",\"three\",\"five\",\"seven\",\"eleven\",\"thirteen\",\"seventeen\",\"nineteen\",\"twenty-three\",\"twenty-nine\",\"thirty-one\",\"thirty-seven\",\"forty-one\",\"forty-three\",\"forty-seven\",\"fifty-three\",\"fifty-nine\",\"sixty-one\",\"sixty-seven\",\"seventy-one\",\"seventy-three\",\"seventy-nine\",\"eighty-three\",\"eighty-nine\",\"ninety-seven\"]}")
                .setHeader("Content-Type", "application/json"));
        // Mock response of Odd URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[31,33,35,37,39,41,43,45,47,49,51],\"strings\":[\"thirty-one\",\"thirty-three\",\"thirty-five\",\"thirty-seven\",\"thirty-nine\",\"forty-one\",\"forty-three\",\"forty-five\",\"forty-seven\",\"forty-nine\",\"fifty-one\"]}")
                .setHeader("Content-Type", "application/json"));
        // Mock response of Fibo URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181],\"strings\":[\"zero\",\"one\",\"one\",\"two\",\"three\",\"five\",\"eight\",\"thirteen\",\"twenty-one\",\"thirty-four\",\"fifty-five\",\"eighty-nine\",\"one hundred forty-four\",\"two hundred thirty-three\",\"three hundred seventy-seven\",\"six hundred ten\",\"nine hundred eighty-seven\",\"one thousand five hundred ninety-seven\",\"two thousand five hundred eighty-four\",\"four thousand one hundred eighty-one\"]}")
                .setHeader("Content-Type", "application/json"));

        List<String> urlList = List.of("/rand", "/primes", "/odd", "/fibo");

        Mono<NumberDto> numberDtoMono = numberService.getNumber(urlList);

        StepVerifier.create(numberDtoMono)
                .expectNextMatches(
                        number -> true)
                .verifyComplete();
    }

    @Test
    void getNumberTest_success_withCache() {
        WebClient webClient = WebClient.builder().build();
        // Create a caffeineCache and put cache value in it
        Cache<String, NumberDto> caffeineCache  = Caffeine.newBuilder().maximumSize(100)
                .expireAfterWrite(Duration.ofSeconds(1)).build();
        caffeineCache.put("/rand", new NumberDto(new TreeSet<>(List.of(44, 99, 83, 76, 3, 36, 64, 15, 67, 28))));

        numberService = new NumberServiceImpl(webClient, caffeineCache);
        // Mock response of Rand URL is empty
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[]}")
                .setHeader("Content-Type", "application/json"));


        List<String> urlList = List.of("/rand");

        Mono<NumberDto> numberDtoMono = numberService.getNumber(urlList);
        StepVerifier.create(numberDtoMono)
                .expectNextMatches(
                        number -> number.getNumbers().size() == 10)
                .verifyComplete();
    }

    @Test
    void getNumberTest_FiboUrlTimeout() {
        // Mock response of Rand URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[44,99,83,76,3,36,64,15,67,28],\"strings\":[\"forty-four\",\"ninety-nine\",\"eighty-three\",\"seventy-six\",\"three\",\"thirty-six\",\"sixty-four\",\"fifteen\",\"sixty-seven\",\"twenty-eight\"]}")
                .setHeader("Content-Type", "application/json"));
        // Mock response of Primes URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97],\"strings\":[\"two\",\"three\",\"five\",\"seven\",\"eleven\",\"thirteen\",\"seventeen\",\"nineteen\",\"twenty-three\",\"twenty-nine\",\"thirty-one\",\"thirty-seven\",\"forty-one\",\"forty-three\",\"forty-seven\",\"fifty-three\",\"fifty-nine\",\"sixty-one\",\"sixty-seven\",\"seventy-one\",\"seventy-three\",\"seventy-nine\",\"eighty-three\",\"eighty-nine\",\"ninety-seven\"]}")
                .setHeader("Content-Type", "application/json"));
        // Mock response of Odd URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[31,33,35,37,39,41,43,45,47,49,51],\"strings\":[\"thirty-one\",\"thirty-three\",\"thirty-five\",\"thirty-seven\",\"thirty-nine\",\"forty-one\",\"forty-three\",\"forty-five\",\"forty-seven\",\"forty-nine\",\"fifty-one\"]}")
                .setHeader("Content-Type", "application/json"));
        // Mock response of Fibo URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181],\"strings\":[\"zero\",\"one\",\"one\",\"two\",\"three\",\"five\",\"eight\",\"thirteen\",\"twenty-one\",\"thirty-four\",\"fifty-five\",\"eighty-nine\",\"one hundred forty-four\",\"two hundred thirty-three\",\"three hundred seventy-seven\",\"six hundred ten\",\"nine hundred eighty-seven\",\"one thousand five hundred ninety-seven\",\"two thousand five hundred eighty-four\",\"four thousand one hundred eighty-one\"]}")
                .setHeader("Content-Type", "application/json").setBodyDelay(6, TimeUnit.SECONDS));

        List<String> urlList = List.of("/rand", "/primes", "/odd", "/fibo");

        Mono<NumberDto> numberDtoMono = numberService.getNumber(urlList);
        StepVerifier.create(numberDtoMono)
                .expectNextMatches(
                        number -> number.getNumbers().first() == 2 && !number.getNumbers().contains(4181))
                .verifyComplete();
    }

    @Test
    void getNumberTest_allUrlTimeout() {
        // Mock response of Rand URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[44,99,83,76,3,36,64,15,67,28],\"strings\":[\"forty-four\",\"ninety-nine\",\"eighty-three\",\"seventy-six\",\"three\",\"thirty-six\",\"sixty-four\",\"fifteen\",\"sixty-seven\",\"twenty-eight\"]}")
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(6, TimeUnit.SECONDS));
        // Mock response of Primes URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97],\"strings\":[\"two\",\"three\",\"five\",\"seven\",\"eleven\",\"thirteen\",\"seventeen\",\"nineteen\",\"twenty-three\",\"twenty-nine\",\"thirty-one\",\"thirty-seven\",\"forty-one\",\"forty-three\",\"forty-seven\",\"fifty-three\",\"fifty-nine\",\"sixty-one\",\"sixty-seven\",\"seventy-one\",\"seventy-three\",\"seventy-nine\",\"eighty-three\",\"eighty-nine\",\"ninety-seven\"]}")
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(6, TimeUnit.SECONDS));
        // Mock response of Odd URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[31,33,35,37,39,41,43,45,47,49,51],\"strings\":[\"thirty-one\",\"thirty-three\",\"thirty-five\",\"thirty-seven\",\"thirty-nine\",\"forty-one\",\"forty-three\",\"forty-five\",\"forty-seven\",\"forty-nine\",\"fifty-one\"]}")
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(6, TimeUnit.SECONDS));
        // Mock response of Fibo URL
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"numbers\":[0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181],\"strings\":[\"zero\",\"one\",\"one\",\"two\",\"three\",\"five\",\"eight\",\"thirteen\",\"twenty-one\",\"thirty-four\",\"fifty-five\",\"eighty-nine\",\"one hundred forty-four\",\"two hundred thirty-three\",\"three hundred seventy-seven\",\"six hundred ten\",\"nine hundred eighty-seven\",\"one thousand five hundred ninety-seven\",\"two thousand five hundred eighty-four\",\"four thousand one hundred eighty-one\"]}")
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(6, TimeUnit.SECONDS));

        List<String> urlList = List.of("/rand", "/primes", "/odd", "/fibo");

        Mono<NumberDto> numberDtoMono = numberService.getNumber(urlList);
        StepVerifier.create(numberDtoMono)
                .expectNextMatches(
                        number -> number.getNumbers().isEmpty())
                .verifyComplete();
    }


}
