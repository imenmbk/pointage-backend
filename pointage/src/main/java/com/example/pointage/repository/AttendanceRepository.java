package com.example.pointage.repository;

import com.example.pointage.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository  extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserId(Long userId); // Trouver les pointages d'un utilisateur
    List<Attendance> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);}
