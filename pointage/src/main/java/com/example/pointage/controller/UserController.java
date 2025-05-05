package com.example.pointage.controller;

import com.example.pointage.model.ChangePasswordRequest;
import com.example.pointage.model.User;
import com.example.pointage.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        logger.info("Requête PATCH /api/users (changePassword) reçue: {}", request);
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllEmployees() {
        logger.info("Requête GET /api/users reçue");
        List<User> employees = service.getAllEmployees();
        logger.info("Utilisateurs renvoyés: {}", employees);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getEmployeeById(@PathVariable Long id) {
        logger.info("Requête GET /api/users/{} reçue", id);
        return service.getEmployeeById(id)
                .map(user -> {
                    logger.info("Utilisateur renvoyé: {}", user);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("Utilisateur avec ID {} non trouvé", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createEmployee(@Valid @RequestBody User user) {
      logger.info("Requête POST /api/users reçue: {}", user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            logger.info("Tentative de création d'utilisateur par: {}", userDetails.getUsername());
            logger.info("Rôles de l'utilisateur: {}", userDetails.getAuthorities());
        } else {
            logger.warn("Impossible de déterminer l'utilisateur ou ses rôles pour la création.");
        }
        try {
            user.setId(null);
            User createdUser = service.createEmployee(user);
            logger.info("Utilisateur créé: {}", createdUser);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateEmployee(@PathVariable Long id, @RequestBody User updatedUser) {
        logger.info("Requête PUT /api/users/{} reçue: {}", id, updatedUser);
        User updated = service.updateEmployee(id, updatedUser);
        logger.info("Utilisateur mis à jour: {}", updated);
        return ResponseEntity.ok(updated);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        logger.info("Requête DELETE /api/users/{} reçue", id);
        service.deleteEmployee(id);
        logger.info("Utilisateur supprimé: {}", id);
        return ResponseEntity.noContent().build();
    }
}