package com.example.number.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.TreeSet;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NumberDto {
    private TreeSet<Integer> numbers;
}
