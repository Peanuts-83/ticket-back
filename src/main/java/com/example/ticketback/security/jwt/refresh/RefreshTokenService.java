package com.example.ticketback.security.jwt.refresh;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.security.jwt.JwtProperties;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final JwtProperties jwtProperties;
    private final SecureRandom secure = new SecureRandom();

    /**
     * Émet un token brut au client et persiste son hash
     */
    @Transactional
    public String createRTokenForUser(User user) {
        String raw = generateToken();
        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(hash(raw));
        rt.setUser(user);
        Instant now = Instant.now();
        rt.setCreatedAt(now);
        rt.setLastUsedAt(now);
        repo.save(rt);
        return raw;
    }

    @Transactional
    public RotationResult rotateRToken(String rawToken) {
        RefreshToken rt = repo.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalide"));
        Instant now = Instant.now();

        // borne absolue
        if (now.isAfter(rt.getCreatedAt().plusMillis(jwtProperties.getRefresh().getMaxSession()))) {
            repo.delete(rt);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expirée - durée max atteinte");
        }
        // borne glissante
        if (now.isAfter(rt.getLastUsedAt().plusMillis(jwtProperties.getRefresh().getIdleTimeout()))) {
            repo.delete(rt);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expirée - inactivité");
        }

        User user = rt.getUser();
        Instant createdAt =  rt.getCreatedAt();
        repo.delete(rt);
        String raw = generateToken();
        RefreshToken next = new RefreshToken();
        next.setTokenHash(hash(raw));
        next.setUser(user);
        next.setCreatedAt(createdAt);       // on garde la date de création pour la borne absolue
        next.setLastUsedAt(now);
        repo.save(next);
        return new RotationResult(raw, user);
    }

    @Transactional
    public void revokeRToken(String rawToken) {
        repo.deleteByTokenHash(String.valueOf(hash(rawToken)));
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secure.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public record RotationResult(String refreshToken, User user) {}
}
