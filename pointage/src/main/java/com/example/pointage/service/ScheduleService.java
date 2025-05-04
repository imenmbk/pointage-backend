package com.example.pointage.service;

import com.example.pointage.model.Schedule;
import com.example.pointage.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public Schedule createSchedule(Schedule schedule) {
        if (!isAdmin()) {
            throw new SecurityException("Accès refusé : seul un ADMIN peut créer un horaire.");
        }
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public void deleteSchedule(Long id) {
        if (!isAdmin()) {
            throw new SecurityException("Accès refusé : seul un ADMIN peut supprimer un horaire.");
        }
        scheduleRepository.deleteById(id);
    }

    public Schedule updateSchedule(Long id, Schedule updatedSchedule) {
        if (!isAdmin()) {
            throw new SecurityException("Accès refusé : seul un ADMIN peut modifier un horaire.");
        }
        return scheduleRepository.findById(id).map(schedule -> {
            schedule.setMonday(updatedSchedule.getMonday());
            schedule.setTuesday(updatedSchedule.getTuesday());
            schedule.setWednesday(updatedSchedule.getWednesday());
            schedule.setThursday(updatedSchedule.getThursday());
            schedule.setFriday(updatedSchedule.getFriday());
            schedule.setSaturday(updatedSchedule.getSaturday());
            schedule.setSunday(updatedSchedule.getSunday());
            schedule.setType(updatedSchedule.getType());
            return scheduleRepository.save(schedule);
        }).orElseThrow(() -> new RuntimeException("Schedule not found"));
    }
}
