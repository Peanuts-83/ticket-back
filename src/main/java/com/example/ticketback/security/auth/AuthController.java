package com.example.ticketback.security.auth;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.dto.common.HttpPostResult;
import com.example.ticketback.repository.UserRepository;
import com.example.ticketback.security.auth.models.LoginRequest;
import com.example.ticketback.security.auth.models.LoginResponse;
import com.example.ticketback.security.auth.models.LogoutRequest;
import com.example.ticketback.security.auth.models.LogoutResponse;
import com.example.ticketback.security.jwt.JwtService;
import com.example.ticketback.security.jwt.refresh.RefreshTokenService;
import com.example.ticketback.service.UserService;
import com.example.ticketback.web.ApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(ApiRoutes.Auth.BASE)
@RequiredArgsConstructor
@Tag(
        name = "Authentification",
        description = "Endpoints liés à la connexion et à la génération du token JWT."
)
public class AuthController {
    public final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping(ApiRoutes.Auth.LOGIN)
    @PreAuthorize("permitAll")
    @SecurityRequirements
    @Operation(
            summary = "[PUBLIC] Connexion utilisateur",
            tags = {"PUBLIC"},
            description = """
                    Authentifie un utilisateur à partir de ses identifiants.
                    
                    Retourne un accessToken JWT à envoyer ensuite dans le header :
                    Authorization: Bearer <accessToken>
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Identifiants de connexion.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "Admin dev",
                                            summary = "Compte ADMIN de développement",
                                            value = """
                                                    {
                                                      "email": "admin@tf.local",
                                                      "password": "Admin1234!"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Utilisateur dev",
                                            summary = "Compte UTILISATEUR de développement",
                                            value = """
                                                    {
                                                      "email": "thomas@tf.local",
                                                      "password": "passWord?1"
                                                    }
                                                    """
                                    )
                            }
                    )
            )

    )
    public HttpPostResult<LoginResponse> login(@RequestBody LoginRequest request) {
        // Rejet si user déjà authentifié
        Authentication l_current = SecurityContextHolder.getContext().getAuthentication();
        if (l_current != null && l_current.isAuthenticated() && !(l_current instanceof AnonymousAuthenticationToken)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Utilisateur déjà connecté");
        }

        // Check du user en base
        User l_user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            l_user.getUsername(),
                            request.password()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            assert userDetails != null;
            String token = jwtService.generateToken(userDetails);
            String refreshToken = refreshTokenService.createRTokenForUser(l_user);
            return HttpPostResult.of(new LoginResponse(token, refreshToken, userService.toDto(l_user)));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte désactivé");
        }
    }

    @PostMapping(ApiRoutes.Auth.LOGOUT)
    @SecurityRequirements
    @Operation(
            summary = "[PUBLIC] Déconnexion utilisateur",
            tags = {"PUBLIC"},
            description = """
                    Confirme la déconnexion. Le token étant stateless, l'invalidation réelle se fait coté client.
                    """
    )
    public HttpPostResult<LogoutResponse> logout(@RequestBody LogoutRequest request) {
        SecurityContextHolder.clearContext();
        refreshTokenService.revokeRToken(request.refreshToken());
        return HttpPostResult.of(new LogoutResponse("Vous êtes déconnecté"));
    }

    @PostMapping(ApiRoutes.Auth.REFRESH)
    @SecurityRequirements
    @Operation(
            summary = "[PUBLIC] Demande de refreshToken",
            tags = {"PUBLIC"},
            description = """
                Demande automatique de refreshToken selon le contexte
                """
    )
    public HttpPostResult<LoginResponse> refresh(@RequestBody RefreshRequest request) {
        RefreshTokenService.RotationResult result = refreshTokenService.rotateRToken(request.refreshToken());
        UserDetails details = userDetailsService.loadUserByUsername(result.user().getUsername());
        String access = jwtService.generateToken(details);
        return HttpPostResult.of(new LoginResponse(access, result.refreshToken(), userService.toDto(result.user())));
    }

    public record RefreshRequest(String refreshToken) {}
}
