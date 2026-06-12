package com.example.ticketback.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthentificationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // Auth existante (ex: @WithMockUser pour les tests) -> on laisse passer
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Api publique -> on laisse passer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Api privée -> validation du token
        try {
            String jwt = authHeader.substring("Bearer ".length());
            String userName = jwtService.extractUsername(jwt);
            System.out.println("[JWT] extracted username = " + userName);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                boolean tokenValid = jwtService.isTokenValid(jwt, userDetails);
//                System.out.println("[JWT] token valid = " + tokenValid);
//                System.out.println("[JWT] authorities = " + userDetails.getAuthorities());
                if (tokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                    System.out.println("[JWT] authentication set for = " + userName);
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
//            System.out.println("[JWT ERROR] class = " + e.getClass().getName());
//            System.out.println("[JWT ERROR] message = " + e.getMessage());
            // Token invalide, expiré etc...
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> body = Map.of(
                    "status", HttpServletResponse.SC_UNAUTHORIZED,
                    "error", "Unauthorized",
                    "message", "Invalid or expired token",
                    "path", request.getRequestURI()
            );

            objectMapper.writeValue(response.getWriter(), body);
            return;
        }

        filterChain.doFilter(request, response);


    }
}
