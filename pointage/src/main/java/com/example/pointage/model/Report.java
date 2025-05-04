package com.example.pointage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @PositiveOrZero(message = "Total employees must be zero or positive")
    private int totalEmployees;

    @PositiveOrZero(message = "Present employees must be zero or positive")
    private int presentEmployees;

    @PositiveOrZero(message = "Absent employees must be zero or positive")
    private int absentEmployees;

    @PositiveOrZero(message = "Attendance rate must be zero or positive")
    private double attendanceRate;

    @OneToMany(mappedBy = "report", fetch = FetchType.EAGER)
    private List<Attendance> attendances = new ArrayList<>();

    public Report(LocalDate date, int totalEmployees, int presentEmployees, int absentEmployees, double attendanceRate) {
        this.date = date;
        this.totalEmployees = totalEmployees;
        this.presentEmployees = presentEmployees;
        this.absentEmployees = absentEmployees;
        this.attendanceRate = attendanceRate;
    }
}