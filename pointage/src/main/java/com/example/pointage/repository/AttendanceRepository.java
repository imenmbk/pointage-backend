package com.example.pointage.repository;

import com.example.pointage.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository  extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserId(Long userId); // Trouver les pointages d'un utilisateur
    List<Attendance> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

@Query("SELECT MONTH(a.checkInTime), COUNT(a.id) FROM Attendance a " +
        "WHERE a.status = 'PRESENT' AND a.user.department = :branche " +
        "GROUP BY MONTH(a.checkInTime)")
List<Object[]> countPresenceByMonthAndBranche(@Param("branche") String branche); }