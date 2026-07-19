package com.example.ticketback.domain.entity;

import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


/**
 * Entité JPA représentant un utilisateur applicatif.
 * Cette classe est persistée en base de données.
 * Elle ne doit pas être exposée directement au front :
 * les controllers doivent retourner des DTOs.
 */

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "app_user")
public class User implements UserDetails {
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
    private UserRole role;

    @Column(nullable = false)
    private LocalDateTime dt_created;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    // url de l'image
    private String avatar;

    public User(String username, String email, String password, UserRole role, LocalDateTime dt_created, UserStatus status, String avatar) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : UserRole.USER;
        this.dt_created = dt_created != null ? dt_created : LocalDateTime.now();
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.avatar = avatar;
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isEnabled() {
        // Seuls les comptes ACTIVE peuvent se connecter
        return status.equals(UserStatus.ACTIVE);
    }

}
