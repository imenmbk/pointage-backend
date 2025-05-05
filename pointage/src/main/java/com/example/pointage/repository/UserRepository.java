package com.example.pointage.repository;

import com.example.pointage.model.Role;
import com.example.pointage.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Déjà existant
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role); // Méthode existante
    List<User> findByDepartment(String department); // Trouver par département
    List<User> findByUsernameContainingOrEmailContaining(String username, String email); // Recherche par chaîne
    boolean existsByUsername(String username); // Vérification existence username
    boolean existsByEmail(String email); // Vérification existence email

    // Requête personnalisée : Trouver par rôle et département
    @Query("SELECT u FROM User u WHERE u.role = ?1 AND u.department = ?2")
    List<User> findByRoleAndDepartment(Role role, String department);

    // Pagination des utilisateurs par rôle
    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT DISTINCT u.department FROM User u")
    List<String> findAllBranches();
}
