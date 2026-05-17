package com.example.ticketback.dto.user;

import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.dto.common.MetaField;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de formulaire utilisateur pour création et modification")
public record UserFormDto(
        @MetaField(libelle = "Identifiant")
        @Schema(
                description = "Identifiant unique, null au create",
                example = "1",
                nullable = true
        )
        Long id,

        @MetaField(libelle = "Nom")
        @Schema(
                description = "nom utilisateur",
                example = "admin"
        )
        String userName,

        @MetaField(libelle = "email")
        @Schema(
                description = "email utilisateur",
                example = "admin@ticketflow.local"
        )
        String email,

        @MetaField(libelle = "Rôle", defaultvalue = "USER")
        @Schema(
                description = "role utilisateur",
                example = "ADMIN"
        )
        UserRole role,

        @MetaField(libelle = "Mot de passe")
        @Schema(
                description = """
                mot de passe utilisateur,
                obligatoire en création,
                optionnel en modification
                """,
                example = "azertyP_1"
        )
        String password,

        @MetaField(libelle = "Avatar")
        @Schema(
                description = "Avatar de l'utilisateur. Peut être une URL, un nom de fichier ou un identifiant de ressource.",
                example = "https://cdn.ticketflow.local/avatars/user-1.png",
                nullable = true
        )
        String avatar
) {
}
