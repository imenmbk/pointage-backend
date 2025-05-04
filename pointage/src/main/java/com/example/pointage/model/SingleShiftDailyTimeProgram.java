package com.example.pointage.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Entity
@DiscriminatorValue("SINGLE_SHIFT")
@Getter
@Setter
@NoArgsConstructor
public class SingleShiftDailyTimeProgram extends DailyTimeProgram {

    private LocalTime start;
    private LocalTime end;
}
