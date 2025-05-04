package com.example.pointage.service;

import com.example.pointage.model.Attendance;
import com.example.pointage.model.AttendanceStatus;
import com.example.pointage.model.Report;
import com.example.pointage.repository.AttendanceRepository;
import com.example.pointage.repository.ReportRepository;
import com.example.pointage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    public Report createReport(LocalDate date, int totalEmployees, int presentEmployees, int absentEmployees, double attendanceRate) {
        if (date == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date cannot be null");
        if (totalEmployees < 0 || presentEmployees < 0 || absentEmployees < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee counts cannot be negative");
        if (presentEmployees + absentEmployees > totalEmployees)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sum of present and absent employees cannot exceed total employees");
        if (attendanceRate < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attendance rate cannot be negative");

        Report newReport = Report.builder()
                .date(date)
                .totalEmployees(totalEmployees)
                .presentEmployees(presentEmployees)
                .absentEmployees(absentEmployees)
                .attendanceRate(attendanceRate)
                .build();

        return reportRepository.save(newReport);
    }

    public Report getReportById(Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report ID cannot be null");

        return reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found with id: " + id));
    }

    public double getDailyScore(LocalDate date) {
        int totalEmployees = (int) userRepository.count();

        int presentEmployees = (int) attendanceRepository.countByCheckInTimeBetween(
                date.atStartOfDay(),
                date.atTime(23, 59, 59)
        );

        return totalEmployees > 0 ? (presentEmployees * 100.0 / totalEmployees) : 0;
    }

    public double getWeeklyScore(LocalDate date) {
        int totalEmployees = (int) userRepository.count();

        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Attendance> attendances = attendanceRepository.findByCheckInTimeBetween(
                startOfWeek.atStartOfDay(),
                endOfWeek.atTime(23, 59, 59)
        );

        long presentEmployees = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        int workDays = 5;
        int totalPossibleAttendances = totalEmployees * workDays;

        return totalPossibleAttendances > 0 ? (presentEmployees * 100.0 / totalPossibleAttendances) : 0;
    }

    public double getMonthlyScore(int year, int month) {
        int totalEmployees = (int) userRepository.count();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        int presentEmployees = (int) attendanceRepository.countByCheckInTimeBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        int totalPossibleAttendances = totalEmployees * startDate.lengthOfMonth();
        return totalPossibleAttendances > 0 ? (presentEmployees * 100.0 / totalPossibleAttendances) : 0;
    }

    public Report generateMonthlyReport(int year, int month) {
        int totalEmployees = (int) userRepository.count();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Attendance> attendances = attendanceRepository.findByCheckInTimeBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        long presentEmployees = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        int totalWorkDays = startDate.lengthOfMonth();
        int totalPossibleAttendances = totalEmployees * totalWorkDays;
        int absentEmployees = (int) (totalPossibleAttendances - presentEmployees);

        double attendanceRate = totalPossibleAttendances > 0
                ? (presentEmployees * 100.0 / totalPossibleAttendances)
                : 0;

        return new Report(startDate, totalEmployees, (int) presentEmployees, absentEmployees, attendanceRate);
    }

    public Report generateDailyReport(LocalDate date) {
        int totalEmployees = (int) userRepository.count();

        List<Attendance> attendances = attendanceRepository.findByCheckInTimeBetween(
                date.atStartOfDay(),
                date.atTime(23, 59, 59)
        );

        long presentEmployees = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        int absentEmployees = totalEmployees - (int) presentEmployees;
        double attendanceRate = totalEmployees > 0
                ? (presentEmployees * 100.0 / totalEmployees)
                : 0;

        return new Report(date, totalEmployees, (int) presentEmployees, absentEmployees, attendanceRate);
    }
}
