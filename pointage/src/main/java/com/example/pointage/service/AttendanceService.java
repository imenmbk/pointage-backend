package com.example.pointage.service;

import com.example.pointage.model.Attendance;
import com.example.pointage.model.AttendanceStatus;
import com.example.pointage.model.Report;
import com.example.pointage.model.User;
import com.example.pointage.repository.AttendanceRepository;
import com.example.pointage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 🔹 Récupérer tous les pointages (ADMIN seulement)
    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    // 🔹 Récupérer le pointage d'un employé spécifique
    public List<Attendance> getAttendancesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        return attendanceRepository.findByUserId(userId);
    }

    // 🔹 Ajouter un pointage (utilisé par l'API de reconnaissance faciale)
    public Attendance createAttendance(Long userId, Attendance attendance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

        attendance.setUser(user);

        // Vérifie que le status est bien un enum valide si jamais ça vient du JSON
        if (attendance.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut de présence invalide ou manquant");
        }

        return attendanceRepository.save(attendance);
    }

    // 🔹 Supprimer un pointage (ADMIN seulement)

    public boolean deleteAttendanceByUserId(Long userId) {
        List<Attendance> attendances = attendanceRepository.findByUserId(userId);
        if (!attendances.isEmpty()) {
            attendanceRepository.deleteAll(attendances);
            return true;
        }
        return false;
    }


}
