package com.example.pointage.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Entity
@DiscriminatorValue("DOUBLE_SHIFT")
@Getter
@Setter
@NoArgsConstructor
public class DoubleShiftDailyTimeProgram extends DailyTimeProgram {
    private LocalTime startFirstShift;
    private LocalTime endFirstShift;
    private LocalTime startSecondShift;
    private LocalTime endSecondShift;
}
