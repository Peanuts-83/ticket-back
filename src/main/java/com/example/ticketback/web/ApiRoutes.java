package com.example.ticketback.web;

/**
 * Source de vérité unique des chemins d'endpoints de l'API.
 * <p>
 * Les constantes sont référencées directement dans les annotations Spring
 * ({@code @RequestMapping} au niveau classe, {@code @PostMapping}/{@code @GetMapping}
 * au niveau méthode) ainsi que dans les tests, afin qu'un renommage de chemin
 * se propage partout depuis un seul endroit.
 */
public final class ApiRoutes {

    private ApiRoutes() {
    }

    /** Authentification — {@code AuthController}. */
    public static final class Auth {
        public static final String BASE  = "/api/auth";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";

        private Auth() {
        }
    }

    /** Contrôle de santé — {@code HealthController} (chemin absolu, pas de base). */
    public static final class Health {
        public static final String HEALTH = "/api/health";

        private Health() {
        }
    }

    /** Utilisateurs — {@code UserController}. */
    public static final class User {
        public static final String BASE        = "/api/user";
        public static final String GET         = "/get/{id}";
        public static final String GET_LIST    = "/getList";
        public static final String GET_UPDATE  = "/getUpdate/{id}";
        public static final String UPDATE      = "/update";
        public static final String META_CREATE = "/metaCreate";
        public static final String CREATE      = "/create";
        public static final String DELETE      = "/delete";

        private User() {
        }
    }

    /** Tickets — {@code TicketController} (list et create mappés sur la base). */
    public static final class Ticket {
        public static final String BASE = "/api/tickets";
        public static final String GET = "/get/{id}";
        public static final String GET_LIST = "/getList";
        public static final String GET_UPDATE  = "/getUpdate/{id}";
        public static final String UPDATE      = "/update";
        public static final String META_CREATE = "/metaCreate";
        public static final String CREATE      = "/create";
        public static final String DELETE      = "/delete";

        private Ticket() {
        }
    }
}
