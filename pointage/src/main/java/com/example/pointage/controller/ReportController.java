package com.example.pointage.controller;

import com.example.pointage.model.Report;
import com.example.pointage.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/create")
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report savedReport = reportService.createReport(
                report.getDate(),
                report.getTotalEmployees(),
                report.getPresentEmployees(),
                report.getAbsentEmployees(),
                report.getAttendanceRate()
        );
        return ResponseEntity.ok(savedReport);
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<Report> generateDailyReport(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseEntity.ok(reportService.generateDailyReport(date));
    }

    @GetMapping("/daily/today")
    public ResponseEntity<Report> generateTodayReport() {
        return ResponseEntity.ok(reportService.generateDailyReport(LocalDate.now()));
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Report> generateMonthlyReport(@PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(reportService.generateMonthlyReport(year, month));
    }

    @GetMapping("/monthly/last")
    public ResponseEntity<Report> generateLastMonthReport() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        return ResponseEntity.ok(reportService.generateMonthlyReport(lastMonth.getYear(), lastMonth.getMonthValue()));
    }

    @GetMapping("/download-pdf/{id}")
    public ResponseEntity<?> downloadReportAsPdf(@PathVariable Long id) {
        return ResponseEntity.status(501).body("PDF generation not implemented yet");
    }

    @GetMapping("/score/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getDailyScore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyScore(date));
    }

    @GetMapping("/score/weekly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getWeeklyScore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getWeeklyScore(date));
    }

    @GetMapping("/score/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getMonthlyScore(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(reportService.getMonthlyScore(year, month));
    }
}
