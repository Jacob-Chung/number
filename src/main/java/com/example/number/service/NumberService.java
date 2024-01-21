package com.example.number.service;

import com.example.number.dto.NumberDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface NumberService {
    public Mono<NumberDto> getNumber(List<String> urlList);
}
