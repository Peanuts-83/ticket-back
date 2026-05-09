package com.example.ticketback.security;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf désactivés pour dev angular
                .csrf(AbstractHttpConfigurer::disable)
                // CORS à venir pour localhost:4200
                .cors(Customizer.withDefaults())
                // JWT -> pas de session serveur
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // endpoints autorisés
                .authorizeHttpRequests(auth -> auth
                        // auth publique
                        .requestMatchers("/api/auth/**").permitAll()
                        // H2 en dev
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        // test de la création avant JWT
                        .requestMatchers("/api/user/metaCreate").permitAll()
                        .requestMatchers("/api/user/create").permitAll()
                        // healthController
                        .requestMatchers("/api/health").permitAll()
                        // accès admin
//                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.toString())
                        // le reste requiert une auth
                        .anyRequest().authenticated()
                )
                // TODO: ouvrir avec JWT, tout fermé par défaut actuellement
//                .oauth2ResourceServer(oauth -> oauth.jwt());
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
                "GET", "POST", "DELETE"
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
