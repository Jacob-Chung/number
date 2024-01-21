package com.example.number.controller;

import com.example.number.dto.NumberDto;
import com.example.number.service.NumberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;


@ExtendWith(MockitoExtension.class)
public class NumberControllerTest {
    @Mock
    NumberService numberService;
    @InjectMocks
    private NumberController numberController;

    @Test
    void testGetNumber_success() throws Exception {
        ReflectionTestUtils.setField(numberController, "timeoutMillis", 5000);
        Mono<NumberDto> result = Mono.just(new NumberDto(new TreeSet<>(List.of(1, 3, 5, 7, 9))));
        Mockito.when(numberService.getNumber(List.of("http://localhost:8090/odd"))).thenReturn(result);

        Mono<NumberDto> actual = numberController.getNumber(List.of("http://localhost:8090/odd"));

        StepVerifier.create(actual)
                .expectNextMatches(number -> number.getNumbers().first() == 1 && number.getNumbers().last() == 9)
                .verifyComplete();

    }

    @Test
    void testGetNumber_timeout_error() throws Exception {
        ReflectionTestUtils.setField(numberController, "timeoutMillis", 1);
        Mono<NumberDto> result = Mono.just(new NumberDto(new TreeSet<>(List.of(1, 3, 5, 7, 9)))).delayElement(java.time.Duration.ofMillis(6000));

        Mockito.when(numberService.getNumber(List.of("http://localhost:8090/odd"))).thenReturn(result);

        StepVerifier.create(numberController.getNumber(List.of("http://localhost:8090/odd")))
                .expectError(TimeoutException.class)
                .verify();

    }
}
