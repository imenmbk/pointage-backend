package com.example.pointage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileScheduleDTO {
    private Long id;
    private Long userId;
    private List<Long> scheduleIds; // Liste des IDs de Schedule
    private String remarks;
    private boolean regular;
}