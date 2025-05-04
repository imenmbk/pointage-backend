package com.example.pointage.mapper;

import com.example.pointage.dto.ProfileScheduleDTO;
import com.example.pointage.dto.ProfileScheduleRequestDTO;
import com.example.pointage.model.ProfileSchedule;
import com.example.pointage.model.Schedule;
import com.example.pointage.model.User;
import com.example.pointage.repository.ScheduleRepository;
import com.example.pointage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileScheduleMapper {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    // Convertir ProfileSchedule en ProfileScheduleDTO
    public ProfileScheduleDTO toDTO(ProfileSchedule profileSchedule) {
        if (profileSchedule == null) {
            return null;
        }

        List<Long> scheduleIds = profileSchedule.getSchedules() != null
                ? profileSchedule.getSchedules().stream()
                .map(Schedule::getId)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new ProfileScheduleDTO(
                profileSchedule.getId(),
                profileSchedule.getUser() != null ? profileSchedule.getUser().getId() : null,
                scheduleIds,
                profileSchedule.getRemarks(),
                profileSchedule.isRegular()
        );
    }

    // Convertir ProfileScheduleDTO en ProfileSchedule
    public ProfileSchedule toEntity(ProfileScheduleDTO dto) {
        if (dto == null) {
            return null;
        }

        ProfileSchedule profileSchedule = new ProfileSchedule();
        profileSchedule.setId(dto.getId());
        profileSchedule.setRemarks(dto.getRemarks());
        profileSchedule.setRegular(dto.isRegular());

        // Associer l'utilisateur
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec ID : " + dto.getUserId()));
            profileSchedule.setUser(user);
        }

        // Associer les schedules
        List<Schedule> schedules = dto.getScheduleIds() != null
                ? dto.getScheduleIds().stream()
                .map(id -> scheduleRepository.findById(id).orElse(null))
                .filter(schedule -> schedule != null)
                .collect(Collectors.toList())
                : Collections.emptyList();
        profileSchedule.setSchedules(schedules);

        return profileSchedule;
    }

    // Convertir ProfileScheduleRequestDTO en ProfileSchedule
    public ProfileSchedule toEntityFromRequest(ProfileScheduleRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        ProfileSchedule profileSchedule = new ProfileSchedule();
        profileSchedule.setRemarks(requestDTO.getRemarks());
        profileSchedule.setRegular(requestDTO.isRegular());

        // Associer l'utilisateur
        if (requestDTO.getUser() != null && requestDTO.getUser().getId() != null) {
            User user = userRepository.findById(requestDTO.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec ID : " + requestDTO.getUser().getId()));
            profileSchedule.setUser(user);
        }

        // Associer les schedules
        List<Schedule> schedules = requestDTO.getSchedules() != null
                ? requestDTO.getSchedules().stream()
                .map(scheduleDTO -> scheduleRepository.findById(scheduleDTO.getId()).orElse(null))
                .filter(schedule -> schedule != null)
                .collect(Collectors.toList())
                : Collections.emptyList();
        profileSchedule.setSchedules(schedules);

        return profileSchedule;
    }
}