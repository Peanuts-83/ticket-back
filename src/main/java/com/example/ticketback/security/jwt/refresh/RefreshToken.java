package com.example.ticketback.security.jwt.refresh;

import com.example.ticketback.domain.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name="refresh_token")
@Getter @Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, comment = "SHA-256 du token brut")
    private String tokenHash;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, comment = "Borne absolue - max session")
    private Instant createdAt;

    @Column(nullable = false, comment = "Borne glissante - idleTimeout")
    private Instant lastUsedAt;
}
