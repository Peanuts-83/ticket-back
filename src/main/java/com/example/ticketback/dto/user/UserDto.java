package com.example.ticketback.dto.user;

import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.domain.enums.UserStatus;
import com.example.ticketback.dto.common.MetaField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Détails d'un utilisateur, pas de password")
public record UserDto (
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

        @MetaField(libelle = "Rôle", defaultValue = "USER")
        @Schema(
                description = "role utilisateur",
                example = "ADMIN"
        )
        UserRole role,

        @MetaField(libelle = "Date et heure de création")
        @Schema(
                description = "Date et heure de création de l'utilisateur au format ISO-8601.",
                example = "2026-05-16T14:35:22"
        )
        LocalDateTime dateHeureCreation,

        @MetaField(libelle = "Statut")
        @Schema(
                description = "Statut fonctionnel de l'utilisateur.",
                example = "ACTIF"
        )
        UserStatus status,

        @MetaField(libelle = "Avatar")
        @Schema(
                description = "Avatar de l'utilisateur. Peut être une URL, un nom de fichier ou un identifiant de ressource.",
                example = "https://cdn.ticketflow.local/avatars/user-1.png",
                nullable = true
        )
        String avatar

) {
}
