package com.example.ticketback.security;

import com.example.ticketback.domain.enums.Role;
import com.example.ticketback.security.jwt.JwtAuthentificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Config de sécurité PROD
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("prod")
public class SecurityConfig {
    private final JwtAuthentificationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // auth publique
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        // Appli de démo, create ouvert et non réservé ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/user/metaCreate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/getUpdate/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/update").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/delete/{id}").authenticated()
                        // accès admin
                        .requestMatchers(HttpMethod.POST, "/api/user/getList").hasRole(Role.ADMIN.name())
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Config CORS globale
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // TODO: Origines autorisées selon serveur
        configuration.setAllowedOrigins(List.of(
                "http://192.168.0.1:4200"
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
        configuration.setAllowCredentials(true);

        // Cache navigateur du preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
