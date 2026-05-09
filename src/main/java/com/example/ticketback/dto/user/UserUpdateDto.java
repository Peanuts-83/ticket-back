package com.example.ticketback.dto.user;

import com.example.ticketback.domain.enums.Role;
import com.example.ticketback.dto.common.MetaField;

// pas de pwd, traité sur une api spécifique /api/auth/change-password
public record UserUpdateDto(
        Long id,
        @MetaField(libelle = "Nom")
        String userName,
        @MetaField(libelle = "email")
        String email,
        @MetaField(libelle = "Rôle", defaultvalue = "USER")
        Role role
) {
}
