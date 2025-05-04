package com.example.pointage.repository;

import com.example.pointage.model.DailyTimeProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTimeProgramRepository extends JpaRepository<DailyTimeProgram, Long> {
}
