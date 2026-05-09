package com.example.ticketback.dto.user;

import com.example.ticketback.domain.enums.Role;

import java.util.List;

public record UserMetaCreateDto (
        List<Role> roles,
        Role defaultRole
) {
}
