package com.example.pointage.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.pointage.configration.JwtUtils;
import com.example.pointage.service.CustomUserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
        logger.debug("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(jwt);
                logger.debug("Extracted username from JWT: {}", username);
            } catch (ExpiredJwtException e) {
                logger.info("Token expiré pour la requête: {} {}. Message: {}",
                        request.getMethod(), request.getRequestURI(), e.getMessage());
                response.setHeader("X-Token-Expired", "true");
            } catch (JwtException e) {
                logger.warn("Erreur dans le token JWT: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Erreur inattendue lors de l'extraction du nom d'utilisateur du JWT", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            try {
                if (jwtUtils.isTokenValid(jwt, userDetails)) {
                    Claims claims = jwtUtils.extractAllClaims(jwt);
                    List<String> authoritiesFromToken = claims.get("authorities", List.class);

                    logger.info("Autorités extraites du token: {}", authoritiesFromToken);

                    boolean hasAdminRole = authoritiesFromToken.stream()
                            .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
                    logger.info("L'utilisateur a-t-il le rôle ROLE_ADMIN? {}", hasAdminRole);

                    if (!authoritiesFromToken.contains("ROLE_ADMIN") && !authoritiesFromToken.contains("ROLE_EMPLOYEE")) {
                        logger.warn("L'utilisateur n'a pas les rôles nécessaires pour accéder à cette ressource.");
                    }

                    Collection<GrantedAuthority> authorities = authoritiesFromToken.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    logger.info("Utilisateur authentifié: {}, autorités: {}", userDetails.getUsername(),
                            authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                }
            } catch (Exception e) {
                logger.error("Erreur lors de l'authentification: {}", e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
