package com.example.ticketback.security.auth;

import com.example.ticketback.dto.common.HttpPostResult;
import com.example.ticketback.security.auth.models.LoginRequest;
import com.example.ticketback.security.auth.models.LoginResponse;
import com.example.ticketback.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentification",
        description = "Endpoints liés à la connexion et à la génération du token JWT."
)
public class AuthController {
    public final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
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
                                                      "username": "admin",
                                                      "password": "passWord?1"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Utilisateur dev",
                                            summary = "Compte UTILISATEUR de développement",
                                            value = """
                                                    {
                                                      "username": "thomas",
                                                      "password": "passWord?1"
                                                    }
                                                    """
                                    )
                            }
                    )
            )

    )
    public HttpPostResult<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        String token = jwtService.generateToken(userDetails);

        return HttpPostResult.of(new LoginResponse(token));
    }

}
