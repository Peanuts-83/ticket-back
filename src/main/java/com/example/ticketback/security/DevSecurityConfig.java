package com.example.ticketback.security;

import com.example.ticketback.domain.enums.Role;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Config de sécurité DEV
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("dev")
public class DevSecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf désactivés pour dev angular
                .csrf(AbstractHttpConfigurer::disable)
                // CORS à venir pour localhost:4200
                .cors(Customizer.withDefaults())
                // Pas de session serveur
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // endpoints autorisés
                .authorizeHttpRequests(auth -> auth
                                // auth publique
                                .requestMatchers("/api/auth/**").permitAll()
                                // healthController
                                .requestMatchers("/api/health").permitAll()
                                // H2 en dev
                                .requestMatchers(PathRequest.toH2Console()).permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/user/metaCreate").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/user/create").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/user/getUpdate/{id}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/user/update").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/user/delete/{id}").authenticated()
                                // accès admin
                                .requestMatchers(HttpMethod.POST, "/api/user/getList").hasRole(Role.ROLE_ADMIN.name())
                                .requestMatchers("/api/admin/**").hasRole(Role.ROLE_ADMIN.name())
                                // le reste requiert une auth
                                .anyRequest().authenticated()
                )
                // affichage console H2 dans un iframe
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    /**
     * Config CORS globale
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200"
        ));

        // Méthodes HTTP autorisées
        configuration.setAllowedOrigins(List.of(
                "GET", "POST", "DELETE", "OPTIONS"
        ));

        // Headers autorisés pour la requête
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        // Headers lisible coté front dans la réponse (Token)
        configuration.setExposedHeaders(List.of("Authorization"));

        // Cookie ou refreshToken > true
        configuration.setAllowCredentials(false);

        // Cache navigateur du preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
