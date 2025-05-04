package com.example.pointage.service;

import com.example.pointage.dto.ProfileScheduleDTO;
import com.example.pointage.mapper.ProfileScheduleMapper;
import com.example.pointage.model.ProfileSchedule;
import com.example.pointage.model.User;
import com.example.pointage.repository.ProfileScheduleRepository;
import com.example.pointage.repository.UserRepository;
import com.example.pointage.exception.ResourceNotFoundException;  // Exception personnalisée
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileScheduleService {

    private final UserRepository userRepository;
    private final ProfileScheduleMapper profileScheduleMapper;
    private final ProfileScheduleRepository profileScheduleRepository;

    // Créer un ProfileSchedule
    public ProfileScheduleDTO createProfileSchedule(ProfileSchedule profileSchedule) {
        profileScheduleRepository.findByUser(profileSchedule.getUser()).ifPresent(existing -> {
            throw new IllegalStateException("Un profil existe déjà pour cet utilisateur.");
        });
        ProfileSchedule saved = profileScheduleRepository.save(profileSchedule);
        return profileScheduleMapper.toDTO(saved);
    }


    // Obtenir tous les ProfileSchedules sous forme de DTO
    public List<ProfileScheduleDTO> getAllProfileSchedules() {
        return profileScheduleRepository.findAll().stream()
                .map(profileScheduleMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtenir un ProfileSchedule par ID (renvoyer un DTO)
    public ProfileScheduleDTO getProfileScheduleById(Long id) {
        ProfileSchedule profileSchedule = profileScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileSchedule non trouvé avec l'ID: " + id));
        return profileScheduleMapper.toDTO(profileSchedule);
    }

    // Supprimer un ProfileSchedule
    public void deleteProfileSchedule(Long id) {
        ProfileSchedule profileSchedule = profileScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfileSchedule non trouvé avec l'ID: " + id));
        profileScheduleRepository.delete(profileSchedule);
    }

    // Obtenir le profil par email
    public ProfileScheduleDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        ProfileSchedule ps = profileScheduleRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun profil trouvé pour cet utilisateur"));
        return profileScheduleMapper.toDTO(ps);
    }

    // Obtenir tous les profils sous forme de DTO
    public List<ProfileScheduleDTO> getAll() {
        return profileScheduleRepository.findAll().stream()
                .map(profileScheduleMapper::toDTO)
                .collect(Collectors.toList());
    }

}
