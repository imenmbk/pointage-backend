package com.example.pointage.controller;

import com.example.pointage.dto.ChartSeriesDto;
import com.example.pointage.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chart")
@CrossOrigin(origins = "http://localhost:4200")
public class BarChartDataController {

    @Autowired
    private ChartService chartService;

    @GetMapping("/monthly")
    public ResponseEntity<List<ChartSeriesDto>> getMonthlyChartData() {
        return ResponseEntity.ok(chartService.getMonthlyPresenceByBranche());
    }
}