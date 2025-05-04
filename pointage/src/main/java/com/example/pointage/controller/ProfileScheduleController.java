package com.example.pointage.controller;

import com.example.pointage.dto.ProfileScheduleDTO;
import com.example.pointage.dto.ProfileScheduleRequestDTO;
import com.example.pointage.model.ProfileSchedule;
import com.example.pointage.service.ProfileScheduleService;
import com.example.pointage.mapper.ProfileScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/profile-schedules")
@RequiredArgsConstructor
public class ProfileScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileScheduleController.class);

    private final ProfileScheduleService profileScheduleService;
    private final ProfileScheduleMapper profileScheduleMapper;

    // Créer un ProfileSchedule
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ProfileScheduleDTO> createProfileSchedule(@RequestBody ProfileScheduleRequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Utilisateur authentifié : {}", userDetails.getUsername());
        logger.info("Création d'un profil pour l'utilisateur ID : {}", requestDTO.getUser().getId());

        ProfileSchedule profileSchedule = profileScheduleMapper.toEntityFromRequest(requestDTO);
        ProfileScheduleDTO saved = profileScheduleService.createProfileSchedule(profileSchedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Obtenir le profil de l'utilisateur connecté
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ProfileScheduleDTO> getMyProfileSchedule(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Récupération du profil pour l'utilisateur : {}", userDetails.getUsername());
        ProfileScheduleDTO profileSchedule = profileScheduleService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(profileSchedule);
    }

    // Obtenir tous les profils (ADMIN uniquement)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileScheduleDTO>> getAllProfileSchedules() {
        logger.info("Récupération de tous les profils de pointage");
        List<ProfileScheduleDTO> all = profileScheduleService.getAll();
        return ResponseEntity.ok(all);
    }

    // Supprimer un profil (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfileSchedule(@PathVariable Long id) {
        logger.info("Suppression du profil de pointage avec ID : {}", id);
        profileScheduleService.deleteProfileSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
