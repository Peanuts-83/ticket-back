package com.example.ticketback;

import com.example.ticketback.controller.UserController;
import com.example.ticketback.repository.UserRepository;
import com.example.ticketback.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Objectif : vérifier que l’application démarre, que les beans principaux sont bien câblés, et éventuellement qu’un endpoint majeur répond dans un contexte Spring complet.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TicketBackApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Le contexte Spring doit démarrer")
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Les beans principaux utilisateur doivent être chargés")
    void shouldLoadUserBeans() {
        assertNotNull(userController);
        assertNotNull(userService);
        assertNotNull(userRepository);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("L'endpoint de la liste des utilisateurs doit répondre au profil ADMIN")
    void shouldReachUserGetListEndPoint() throws Exception {
        mockMvc.perform(post("/api/user/getList")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }
}
