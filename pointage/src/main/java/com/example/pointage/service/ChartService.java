package com.example.pointage.service;

import com.example.pointage.dto.ChartSeriesDto;
import com.example.pointage.repository.AttendanceRepository;
import com.example.pointage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChartService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ChartSeriesDto> getMonthlyPresenceByBranche() {
        List<String> branches = userRepository.findAllBranches();
        List<ChartSeriesDto> result = new ArrayList<>();

        for (String branche : branches) {
            List<Integer> monthlyCounts = new ArrayList<>(Collections.nCopies(12, 0));
            List<Object[]> rawData = attendanceRepository.countPresenceByMonthAndBranche(branche);
            for (Object[] row : rawData) {
                Integer month = (Integer) row[0];
                Long count = (Long) row[1];
                monthlyCounts.set(month - 1, count.intValue());
            }
            result.add(new ChartSeriesDto(branche, monthlyCounts));
        }
        return result;
    }
}

