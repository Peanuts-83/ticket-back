package com.example.ticketback.admin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class StartupLogger {
    private final Environment environment;

    @PostConstruct
    public void logProfiles() {
        System.out.println("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
    }

}
