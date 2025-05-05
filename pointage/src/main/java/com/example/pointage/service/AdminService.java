package com.example.pointage.service;

import com.example.pointage.model.Role;
import com.example.pointage.model.User;
import com.example.pointage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        return userRepository.findById(id).map(user -> {
            user.setEmail(updatedUser.getEmail());
            user.setLastname(updatedUser.getLastname());
            user.setDepartment(updatedUser.getDepartment());
            user.setProfilePicture(updatedUser.getProfilePicture());
            user.setRole(Role.ADMIN); // écrase toujours le rôle

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrateur non trouvé"));
    }

    public void deleteAdmin(Long id) {
        userRepository.deleteById(id);
    }
}
