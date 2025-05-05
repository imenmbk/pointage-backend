package com.example.pointage.controller;
import lombok.extern.slf4j.Slf4j;

import com.example.pointage.model.User;
import com.example.pointage.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;


    // Ajouter un nouvel administrateur
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<User> createAdmin(@RequestBody User user) {

        try {
            user.setId(null);

            User createdAdmin = adminService.createAdmin(user);
            return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Obtenir un administrateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getAdminById(@PathVariable Long id) {
        User admin = adminService.getAdminById(id);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    // Obtenir tous les administrateurs
    @GetMapping
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = adminService.getAllAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    // Mettre à jour un administrateur
    @PutMapping("/{id}")
    public ResponseEntity<User> updateAdmin(@PathVariable Long id, @RequestBody User updatedUser) {
        log.info("Requête PUT /api/admins/{} reçue: {}", id, updatedUser);
        User updated = adminService.updateAdmin(id, updatedUser);
        log.info("Utilisateur mis à jour: {}", updated);
        return ResponseEntity.ok(updated);
    }




    // Supprimer un administrateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();  // Réponse 204 No Content si la suppression est réussie
    }

}
