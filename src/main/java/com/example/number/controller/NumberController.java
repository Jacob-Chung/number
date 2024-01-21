package com.example.number.controller;

import com.example.number.dto.NumberDto;
import com.example.number.service.NumberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
public class NumberController {
    @Value("${number.timeoutMillis}")
    private int timeoutMillis;

    private NumberService numberService;

    NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @GetMapping("/number")
    public Mono<NumberDto> getNumber(@RequestParam List<String> urlList) {
       return numberService.getNumber(urlList).timeout(Duration.ofMillis(timeoutMillis));
    }

}
