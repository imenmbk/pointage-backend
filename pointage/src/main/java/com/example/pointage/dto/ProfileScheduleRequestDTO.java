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
public class ProfileScheduleRequestDTO {
    private UserIdDTO user;
    private List<ScheduleIdDTO> schedules;
    private String remarks;
    private boolean regular;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserIdDTO {
        private Long id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleIdDTO {
        private Long id;
    }
}