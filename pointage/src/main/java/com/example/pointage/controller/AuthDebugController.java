package com.example.pointage.controller;

import com.example.pointage.configration.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth-debug")
public class AuthDebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthDebugController.class);
    
    private final JwtUtils jwtUtils;
    
    public AuthDebugController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        logger.info("Utilisateur {} a les autorités suivantes: {}", authentication.getName(), authorities);
        
        boolean isAdmin = authorities.contains("ROLE_ADMIN");
        logger.info("L'utilisateur a-t-il le rôle ADMIN? {}", isAdmin);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authenticated", authentication.isAuthenticated());
        userInfo.put("principal", authentication.getPrincipal().toString());
        userInfo.put("name", authentication.getName());
        userInfo.put("authorities", authorities);
        userInfo.put("isAdmin", isAdmin);
        
        return ResponseEntity.ok(userInfo);
    }
    
    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            response.put("error", "Token absent ou format invalide");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        String token = authHeader.substring(7);
        
        try {
            Claims claims = jwtUtils.extractAllClaims(token);
            String username = claims.getSubject();
            List<String> authorities = claims.get("authorities", List.class);
            List<String> roles = claims.get("roles", List.class);
            
            boolean hasAdminRole = authorities.stream()
                    .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
            
            logger.info("Token pour {} contient les autorités: {}", username, authorities);
            logger.info("Token contient-il ROLE_ADMIN? {}", hasAdminRole);
            
            response.put("valid", true);
            response.put("username", username);
            response.put("authorities", authorities);
            response.put("roles", roles);
            response.put("hasAdminRole", hasAdminRole);
            response.put("expiration", claims.getExpiration());
            response.put("issuedAt", claims.getIssuedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("valid", false);
            response.put("error", "Erreur lors de l'analyse du token");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/admin-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkAdminAccess() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Vous avez accès à cette ressource en tant qu'ADMIN");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/token-status")
    public ResponseEntity<?> checkTokenStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            response.put("error", "Token absent ou format invalide");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        String token = authHeader.substring(7);
        
        try {
            String username = jwtUtils.extractUsername(token);
            response.put("valid", true);
            response.put("username", username);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getCause() != null && e.getCause().getMessage().contains("expired")) {
                response.put("valid", false);
                response.put("error", "Token expiré");
                response.put("message", "Veuillez vous reconnecter pour obtenir un nouveau token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
                response.put("valid", false);
                response.put("error", "Token invalide");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("valid", false);
            response.put("error", "Erreur lors de la validation du token");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

