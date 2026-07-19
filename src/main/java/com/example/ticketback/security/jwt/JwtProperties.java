package com.example.ticketback.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
@Getter @Setter
public class JwtProperties {
    private String secret;
    private Long expiration;
    private Refresh refresh = new Refresh();

    @Getter @Setter
    public static class Refresh {
        private Long idleTimeout;
        private Long maxSession;
    }
}
