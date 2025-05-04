package com.example.pointage.controller;

import com.example.pointage.model.Attendance;
import com.example.pointage.model.Report;
import com.example.pointage.model.User;
import com.example.pointage.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Attendance>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Attendance>> getAttendancesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Attendance> createAttendance(
            @PathVariable Long userId,
            @RequestBody Attendance attendance,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Utilisateur authentifié : {}", userDetails.getUsername());
        logger.info("Création d'un pointage pour l'utilisateur ID : {}", userId);

        Attendance savedAttendance = attendanceService.createAttendance(userId, attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long userId) {
        boolean isDeleted = attendanceService.deleteAttendanceByUserId(userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }


}
