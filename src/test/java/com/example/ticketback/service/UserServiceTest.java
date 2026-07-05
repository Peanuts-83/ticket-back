package com.example.ticketback.service;
import com.example.ticketback.domain.entity.User;
import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.domain.enums.UserStatus;
import com.example.ticketback.dto.common.BaseHttpParamList;
import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.common.ViewDataType;
import com.example.ticketback.dto.user.UserDto;
import com.example.ticketback.dto.user.UserListDto;
import com.example.ticketback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;

    private User adminUser;
    private User standardUser;
    private List<User> users;



    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("tested");
        adminUser.setEmail("test@ft.here");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setDt_created(LocalDateTime.now());
        adminUser.setStatus(UserStatus.ACTIVE);

        standardUser = new User();
        standardUser.setUsername("standard");
        standardUser.setPassword("tested");
        standardUser.setEmail("test@ft.here");
        standardUser.setRole(UserRole.USER);
        standardUser.setDt_created(LocalDateTime.now());
        standardUser.setStatus(UserStatus.ACTIVE);

        users = new ArrayList<>(List.of(adminUser, standardUser));
    }

    @Test
    @DisplayName("Doit retourner un utilisateur par id")
    void shouldGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // Act
        UserDto result = userService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(adminUser.getId(), result.id());
        assertEquals(adminUser.getUsername(), result.userName());
        assertEquals(adminUser.getEmail(), result.email());
    }

    @Test
    @DisplayName("Doit retourner une exception si l'utilisateur n'existe pas")
    void shouldThrowExceptionForUnknownUser() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.get(3L));
        assertEquals("User not found with id: 3", exception.getMessage());
    }

    @Test
    @DisplayName("Doit retourner une liste d'utilisateurs")
    void shouldGetAllUsers() {
        when(userRepository.findList(any(Pageable.class))).thenReturn(users);

        List<UserListDto> result = userService.getList(null);

        assertNotNull(result);
        assertEquals(users.size(), result.size());
        assertEquals(users.get(0).getUsername(), result.get(0).userName());
        assertEquals(users.get(1).getRole(), result.get(1).role());
    }

}
