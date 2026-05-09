package com.example.ticketback.dto.user;

import com.example.ticketback.domain.enums.Role;
import com.example.ticketback.dto.common.MetaField;

// Id généré auto par la base
public record UserCreateDto (
        @MetaField(libelle = "Nom")
        String userName,
        @MetaField(libelle = "email")
        String email,
        @MetaField(libelle = "Mot de passe")
        String password,
        @MetaField(libelle = "Rôle", defaultvalue = "USER")
        Role role
) {
}
