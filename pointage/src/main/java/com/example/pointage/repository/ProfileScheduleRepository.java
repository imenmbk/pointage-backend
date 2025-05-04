package com.example.pointage.repository;

import com.example.pointage.model.ProfileSchedule;
import com.example.pointage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileScheduleRepository extends JpaRepository<ProfileSchedule, Long> {
    Optional<ProfileSchedule> findByUser(User user); // ✅ Ajoute cette méthode personnalisée
}
