package com.example.pointage.repository;

import com.example.pointage.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Tu peux ajouter des méthodes personnalisées ici si nécessaire
}
