package com.example.ticketback.domain.entity;

import com.example.ticketback.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

}
