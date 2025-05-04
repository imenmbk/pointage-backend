package com.example.pointage.service;

import com.example.pointage.model.ChangePasswordRequest;
import com.example.pointage.model.Role;
import com.example.pointage.model.User;
import com.example.pointage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        System.out.println("Changement de mot de passe pour l'utilisateur: " + user.getEmail());

        // Check if the old password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            System.out.println("Mot de passe actuel incorrect");
            throw new IllegalArgumentException("Wrong password");
        }
        // Check if the new password matches the confirmation
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            System.out.println("Les nouveaux mots de passe ne correspondent pas");
            throw new IllegalArgumentException("Passwords do not match");
        }
        // Update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        System.out.println("Nouveau mot de passe défini pour l'utilisateur: " + user.getEmail());

        // Save the user
        repository.save(user);
        System.out.println("Utilisateur sauvegardé après changement de mot de passe");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Recherche de l'utilisateur par email: " + username);
        return repository.findByEmail(username)
                .orElseThrow(() -> {
                    System.out.println("Utilisateur non trouvé: " + username);
                    return new UsernameNotFoundException("User not found");
                });
    }

    // 🔹 1. Récupérer tous les employés
    public List<User> getAllEmployees() {
        System.out.println("Récupération de tous les employés");
        List<User> employees = repository.findByRole(Role.EMPLOYEE);
        System.out.println("Employés trouvés: " + employees);
        return employees;
    }
    // 🔹 1.1 Récupérer tous les users par Role
    public List<User> getUsersByRole(Role role) {
        System.out.println("Récupération de tous les users par role");
        List<User> users = repository.findByRole(role);
        System.out.println("Users trouvés: " + users);
        return users;
    }

    // 🔹 2. Récupérer un employé par ID
    public Optional<User> getEmployeeById(Long id) {
        System.out.println("Recherche de l'employé avec ID: " + id);
        Optional<User> employee = repository.findById(id);
        if (employee.isPresent()) {
            System.out.println("Employé trouvé: " + employee.get());
        } else {
            System.out.println("Employé non trouvé avec ID: " + id);
        }
        return employee;
    }

    // 2.2 Récupérer un User par ID
    public Optional<User> getUserById(Long id) {
        System.out.println("Recherche de l'User avec ID: " + id);
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            System.out.println("User trouvé: " + user.get());
        } else {
            System.out.println("User non trouvé avec ID: " + id);
        }
        return user;
    }

    // 🔹 3. Créer un nouvel employé
    public User createEmployee(User user) {
        System.out.println("Création d'un nouvel employé: " + user);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hachage du mot de passe
        user.setRole(Role.EMPLOYEE); // Assigner le rôle EMPLOYEE
        User savedUser = repository.save(user);
        System.out.println("Employé créé avec succès: " + savedUser);
        return savedUser;
    }

    public User updateEmployee(Long id, User updatedUser) {
        System.out.println("Mise à jour de l'employé avec ID: " + id + ", données: " + updatedUser);

        // Récupérer l'utilisateur de la base de données
        User user = repository.findById(id).orElseThrow(() -> {
            System.out.println("Employé non trouvé avec ID: " + id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé");
        });

        // Mise à jour des champs de l'utilisateur
        user.setEmail(updatedUser.getEmail());
        user.setLastname(updatedUser.getLastname());
        user.setDepartment(updatedUser.getDepartment());
        user.setProfilePicture(updatedUser.getProfilePicture());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Sauvegarde de l'utilisateur mis à jour
        User updated = repository.save(user);
        System.out.println("Employé mis à jour avec succès: " + updated);
        return updated;
    }


    // 🔹 5. Supprimer un employé
    public void deleteEmployee(Long id) {
        System.out.println("Suppression de l'employé avec ID: " + id);
        if (!repository.existsById(id)) {
            System.out.println("Employé non trouvé avec ID: " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé");
        }
        repository.deleteById(id);
        System.out.println("Employé supprimé avec succès: " + id);
    }
}