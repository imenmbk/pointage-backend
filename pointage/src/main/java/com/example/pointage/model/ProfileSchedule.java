package com.example.pointage.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "profile_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Correspond à la colonne `id`

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private User user;

    @OneToMany(mappedBy = "profileSchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Schedule> schedules; // Relation corrigée

    private String remarks; // Remarques

    private boolean regular; // Indicateur pour savoir si c'est régulier
}