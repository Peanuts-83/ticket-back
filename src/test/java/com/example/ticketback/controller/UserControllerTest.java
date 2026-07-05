package com.example.ticketback.controller;

import com.example.ticketback.domain.enums.UserRole;
import com.example.ticketback.domain.enums.UserStatus;
import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.user.UserDto;
import com.example.ticketback.dto.user.UserListDto;
import com.example.ticketback.security.jwt.JwtAuthentificationFilter;
import com.example.ticketback.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthentificationFilter jwtAuthentificationFilter;

    @Test
    @DisplayName("api/user/getList appelle l'endpoint de la liste des utilisateurs")
    void shouldCallGetList() throws Exception {
        mockMvc.perform(post("/api/user/getList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getList(nullable(BaseHttpParams.class));
    }

    @Test
    @DisplayName("api/user/getList retourne une liste d'utilisateurs")
    void shouldReturnUserList() throws Exception {
        List<UserListDto> users = List.of(
                new UserListDto(1L, "admin", "admin@test.fr", UserRole.ADMIN, LocalDateTime.now(), UserStatus.ACTIVE),
                new UserListDto(2L, "standard", "standard@test.fr", UserRole.USER, LocalDateTime.now(), UserStatus.ACTIVE)
        );

        when(userService.getList(nullable(BaseHttpParams.class))).thenReturn(users);

        mockMvc.perform(post("/api/user/getList")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].email").value("admin@test.fr"))
                .andExpect(jsonPath("$.data[1].role").value(UserRole.USER.name()))
                .andExpect(jsonPath("$.data[1].userName").value("standard"));
    }

}
