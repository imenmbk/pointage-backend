package com.example.pointage.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DailyTimeProgram monday;

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private DailyTimeProgram tuesday;

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private DailyTimeProgram wednesday;

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private DailyTimeProgram thursday;

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private DailyTimeProgram friday;

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private DailyTimeProgram saturday;

    @OneToOne(cascade = CascadeType.ALL)
    private DailyTimeProgram sunday;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    @OneToMany(mappedBy = "schedule" , fetch = FetchType.EAGER)
    private List<Attendance> attendances;

    @ManyToOne(fetch = FetchType.EAGER )
    @JoinColumn(name = "profile_schedule_id")
    private ProfileSchedule profileSchedule;

    @ManyToOne(fetch = FetchType.EAGER )
    @JoinColumn(name = "user_id")
    private User user;
}
