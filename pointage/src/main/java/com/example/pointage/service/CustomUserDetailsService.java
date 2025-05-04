package com.example.pointage.service;

import com.example.pointage.model.User;
import com.example.pointage.model.Role;
import com.example.pointage.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Tentative de chargement de l'utilisateur avec l'email : " + email);

        // Recherche de l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        System.out.println("Utilisateur trouvé : " + user.getEmail());
        System.out.println("Mot de passe (hashé) : " + user.getPassword());

        Role role = user.getRole();
        List<SimpleGrantedAuthority> authorities = role.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission())) // 🔧 correction ici
                .collect(Collectors.toList());

        // Ajout du rôle avec préfixe ROLE_
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}