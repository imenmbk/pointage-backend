package com.example.pointage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ChartSeriesDto {
    private String name;
    private List<Integer> data;
}