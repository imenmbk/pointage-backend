package com.example.pointage.configration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private Long expirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private Long refreshExpirationTime;

    private SecretKey signingKey;

    private final Clock clock = Clock.systemDefaultZone();
    private final long clockSkewMillis = 300000; // 5 minutes de tolérance

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes;
            if (secretKey.length() < 32) {
                throw new IllegalArgumentException("La clé secrète doit contenir au moins 32 caractères !");
            } else if (secretKey.matches("^[A-Za-z0-9+/=]+$")) {
                // Si la clé est en Base64, la décoder
                keyBytes = Base64.getDecoder().decode(secretKey);
            } else {
                // Sinon, utiliser les bytes bruts
                keyBytes = secretKey.getBytes();
            }
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Clé secrète invalide : la clé doit être d'au moins 32 caractères", e);
            throw new RuntimeException("Clé secrète invalide", e);
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation de la clé secrète", e);
            throw new RuntimeException("Erreur lors de l'initialisation de la clé secrète", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // S'assurer que toutes les autorités ont le format correct
        List<String> authoritiesList = authorities.stream()
                .map(authority -> {
                    String auth = authority.getAuthority();
                    // S'assurer que les rôles ont le préfixe ROLE_
                    if ((auth.equals("ADMIN") || auth.equals("EMPLOYEE")) && !auth.startsWith("ROLE_")) {
                        log.info("Ajout du préfixe ROLE_ au rôle {}", auth);
                        return "ROLE_" + auth;
                    }
                    return auth;
                })
                .collect(Collectors.toList());

        claims.put("authorities", authoritiesList);

        // Stocker les rôles spécifiques pour une vérification plus facile
        List<String> roles = authoritiesList.stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .collect(Collectors.toList());
        claims.put("roles", roles);

        log.debug("Generating token for user: {} with authorities: {}", userDetails.getUsername(), authoritiesList);

        return createToken(claims, userDetails.getUsername(), expirationTime);
    }

    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, refreshExpirationTime);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        if (!isValid) {
            log.warn("Token validation failed for user: {}", username);
        }
        return isValid;
    }

    /**
     * Méthode améliorée pour valider un token sans générer d'exception
     * @param token le token JWT à valider
     * @param userDetails les détails de l'utilisateur
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);

            // Vérifier si l'utilisateur a le rôle ADMIN dans le token
            Claims claims = extractAllClaims(token);
            List<String> authorities = claims.get("authorities", List.class);

            boolean hasAdminRole = authorities.stream()
                    .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

            log.info("Token pour l'utilisateur {} contient le rôle ADMIN: {}", username, hasAdminRole);

            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.info("Token expiré pour l'utilisateur: {}", e.getClaims().getSubject());
            return false;
        } catch (JwtException e) {
            log.warn("Token JWT invalide: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token", e);
            return false;
        }
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpirationDate(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setClock(() -> Date.from(clock.instant()))
                    .setAllowedClockSkewSeconds(clockSkewMillis / 1000)
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Logguer les informations du token pour débogage
            log.debug("Claims extraites du token: {}", claims);

            return claims;
        } catch (ExpiredJwtException e) {
            log.debug("Le token JWT a expiré: {}", e.getMessage());
            throw new IllegalArgumentException("Le token JWT a expiré. Veuillez vous reconnecter.", e);
        }
    }
}

