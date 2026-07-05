package com.example.ticketback.service;

import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ActiveProfiles("test")
@Import(UserServiceSecurityTest.MethodSecurityTestConfig.class)
public class UserServiceSecurityTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private final BaseHttpParams params = new BaseHttpParams(null, null, null, null);

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Un utilisateur au profil 'ADMIN' a accès à la liste des utilisateurs")
    void shouldAllowAdminForUserList() {
        assertDoesNotThrow(() -> userService.getList(params));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Un utilisateur au profil 'USER' n'a pas accès à la liste des utilisateurs")
    void shouldRejectUserForUserList() throws Exception {
        assertThrows(AuthorizationDeniedException.class, () -> userService.getList(params));
    }

    @Test
    @DisplayName("Un utilisateur non authentifié n'a pas accès à la liste des utilisateurs")
    void shouldRejectAnonymousForUserList() throws Exception {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.getList(params));
    }


    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }

}
