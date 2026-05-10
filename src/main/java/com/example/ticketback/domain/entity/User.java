package com.example.ticketback.domain.entity;

import com.example.ticketback.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * Entité JPA représentant un utilisateur applicatif.
 * Cette classe est persistée en base de données.
 * Elle ne doit pas être exposée directement au front :
 * les controllers doivent retourner des DTOs.
 */

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    // à terme, il devra contenir un hash BCrypt
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.ROLE_USER;
    }
}
