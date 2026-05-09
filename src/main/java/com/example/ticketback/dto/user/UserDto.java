package com.example.ticketback.dto.user;

import com.example.ticketback.domain.enums.Role;
import com.example.ticketback.dto.common.MetaField;

/**
 * DTO retourné par un User. Jamais de pwd.
 * @param id
 * @param userName
 * @param email
 * @param role
 */
public record UserDto (
        Long id,
        @MetaField(libelle = "Nom")
        String userName,
        @MetaField(libelle = "email")
        String email,
        @MetaField(libelle = "Rôle", defaultvalue = "USER")
        Role role
) {
}
