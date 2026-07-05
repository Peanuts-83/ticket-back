package com.example.ticketback.repository;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Doit enregistrer et retrouver un utilisateur par username, email ou id")
    void shouldCreateAndFindByUsernameOrEmailOrId() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("tested");
        user.setEmail("test@ft.here");
        user.setRole(UserRole.USER);
        user.setDt_created(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        Optional<User> userFoundByName = userRepository.findByUsername("test");
        assertTrue(userFoundByName.isPresent());
        assertEquals(user.getUsername(), userFoundByName.get().getUsername());
        assertEquals(user.getEmail(), userFoundByName.get().getEmail());
        assertEquals(user.getRole(), userFoundByName.get().getRole());

        Optional<User> userFoundByEmail = userRepository.findByEmail("test@ft.here");
        assertTrue(userFoundByEmail.isPresent());
        assertEquals(user.getUsername(), userFoundByEmail.get().getUsername());

        Optional<User> userFoundById = userRepository.findById(savedUser.getId());
        assertTrue(userFoundById.isPresent());
        assertEquals(user.getUsername(), userFoundById.get().getUsername());
    }

    @Test
    @DisplayName("Doit retourner Optional.empty si l'utilisateur n'est pas trouvé")
    void shouldReturnEmptyForUnknownUser() {
        assertEquals(Optional.empty(), userRepository.findById(3L));
    }

    @Test
    @DisplayName(("Doit retourner Optional.empty si l'email est inconnu"))
    void shouldReturnEmptyForUnknownEmail() {
        assertEquals(Optional.empty(), userRepository.findByEmail("test@none.com"));
    }

    @Test
    @DisplayName(("Doit retourner Optional.empty si le username est inconnu"))
    void shouldReturnEmptyForUnknownUsername() {
        assertEquals(Optional.empty(), userRepository.findByUsername("unknown"));
    }

}
