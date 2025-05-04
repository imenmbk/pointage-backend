package com.example.pointage.configration;

import com.example.pointage.filter.JwtFilter;
import com.example.pointage.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtFilter jwtFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    // Bean pour le PasswordEncoder (utilisé pour l'encodage des mots de passe)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean pour l'AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Bean pour le AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean pour le gestionnaire d'accès refusé personnalisé
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            logger.warn("Accès refusé: {} pour l'URL: {} - Message: {}",
                    request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Utilisateur anonyme",
                    request.getRequestURI(),
                    accessDeniedException.getMessage());

            AccessDeniedHandlerImpl defaultHandler = new AccessDeniedHandlerImpl();
            defaultHandler.handle(request, response, accessDeniedException);
        };
    }

    // Bean pour la configuration CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Activez CORS avec la configuration définie
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Authentification (accessible à tous)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth-debug/**").permitAll()

                        // ==== SCHEDULES ====
                        .requestMatchers(HttpMethod.GET, "/api/schedules/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/schedules/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")

                        // ==== DAILY PROGRAMS ====
                        .requestMatchers(HttpMethod.GET, "/api/daily-programs/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/daily-programs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/daily-programs/**").hasRole("ADMIN")

                        // ==== PROFILE SCHEDULES ====
                        .requestMatchers(HttpMethod.GET, "/api/profile-schedules/me").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/profile-schedules").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/profile-schedules/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/api/profile-schedules/**").hasRole("ADMIN")

                        // ==== ATTENDANCES ====
                        .requestMatchers(HttpMethod.GET, "/api/attendances").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/attendances/user/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/attendances/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/api/attendances/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/attendances/daily").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/attendances/monthly").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/attendances/daily-score").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/attendances/weekly-score").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/attendances/monthly-score").hasRole("ADMIN")

                        // ==== REPORTS ====
                        .requestMatchers(HttpMethod.POST, "/api/report/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/report/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/report/daily").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/report/monthly").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/report/daily-score").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/report/weekly-score").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/report/monthly-score").hasRole("ADMIN")

                        // Autres endpoints protégés
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}