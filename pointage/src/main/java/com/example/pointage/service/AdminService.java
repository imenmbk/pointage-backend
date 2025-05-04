package com.example.pointage.service;

import com.example.pointage.model.Role;
import com.example.pointage.model.User;
import com.example.pointage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Créer un nouvel administrateur
    public User createAdmin(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ADMIN);

        return userRepository.save(user);
    }

    public User getAdminById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllAdmins() {
        return userRepository.findAll();
    }

    public User updateAdmin(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec ID: " + id));

        // Met à jour uniquement les champs nécessaires
        user.setEmail(updatedUser.getEmail());

        // Encode le mot de passe uniquement s'il a changé
        if (!passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        user.setRole(updatedUser.getRole());

        return userRepository.save(user);
    }


    public void deleteAdmin(Long id) {
        userRepository.deleteById(id);
    }
}
