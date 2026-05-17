package com.example.ticketback.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI pour la documentation Swagger de TicketFlow.
 * Objectifs :
 * - Décrire l'API backend.
 * - Déclarer le mécanisme JWT Bearer.
 * - Permettre de tester les endpoints protégés depuis Swagger UI.
 */
@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI ticketFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TicketFlow Back API")
                        .description(
                                """
                                        API backend de TicketFlow.
                                        
                                        Convention utilisée :
                                        - API action-based.
                                        - Endpoints de type /get, /getList, /getUpdate, /update, /metaCreate, /create.
                                        - Les endpoints create/update reçoivent les données dans payload.data.
                                        - Authentification JWT via Authorization: Bearer <accessToken>.
                                        """)
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("Thomas Ranque")
                                .email("tranque@free.fr"))
                )
                // Applique la sécurité dans la documentation
                // les endpoints "permitAll" sont appelables sans token
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }


    @Bean
    public GroupedOpenApi authAndUserApi() {
        return GroupedOpenApi.builder()
                .group("auth-user")
                .pathsToMatch(
                        "/api/**"
                )
                .build();
    }

}
