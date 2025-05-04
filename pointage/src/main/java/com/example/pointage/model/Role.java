package com.example.pointage.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Role {
    EMPLOYEE(
            Set.of(
                    Permission.EMPLOYEE_READ,
                    Permission.EMPLOYEE_UPDATE,
                    Permission.EMPLOYEE_DELETE,
                    Permission.EMPLOYEE_CREATE
            )
    ),
    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_DELETE,
                    Permission.ADMIN_CREATE,
                    Permission.EMPLOYEE_READ,
                    Permission.EMPLOYEE_UPDATE,
                    Permission.EMPLOYEE_DELETE,
                    Permission.EMPLOYEE_CREATE
            )
    );




    private final Set<Permission> permissions;


    public static Role fromString(String role) {
        if (role != null) {
            for (Role r : Role.values()) {
                if (role.equalsIgnoreCase(r.name())) {
                    return r;
                }
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
    // Ajout de la méthode getAuthorities
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name())); // Ajout du rôle ici
        return authorities;
    }


}