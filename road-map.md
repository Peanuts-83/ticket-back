# Road map

## Phase 1 - mise en place

### Choix techniques

- SpringBoot (Java 17)
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- Spring Security (JWT)
- H2 / PostgreSQL (MySQL)
- Maven
- Deps: Lombok, SpringBoot devTools

### Controleur

- @RestController
- Pagination standard
- DTO (pas d'entité exposée)

### Service

- Contient la logique métier

### Model

- Mapping JPA
- Enum persisté
- lazy loading

### Securité

- Csrf
- JWT

### DB

- H2 en mémoire (dev) 
- Chargement auto d'un jeu de données (data-dev.sql)

---

## Phase 2 — Stabiliser le contrat API générique

### Standards d'échange

- `BaseHttpParams`
- `BaseHttpParamList`
- `ViewDataType`
- `HttpPostResult<T>`

---

## Phase 3 — User minimal

- Entity `User`.
- Repository `UserRepository`.
- Service `UserService`.
- Controller `UserController` avec endpoints conventionnés.
- DTOs minimaux.

---

## Phase 4 — Security / filterChain

Implémentation de 2 config de sécurité: dev et prod.

- `SecurityFilterChain`.
  - @EnableMethodSecurity
  - CORS
  - APIs public/privé
  - H2 console en dev.
  - Préparation JWT.

---

## Phase 5 — Security JWT

- Service de génération access token.
- Clé JWT forte Base64 compatible HS256.
- Filtre de validation JWT.
- Header `Authorization: Bearer <accessToken>`.
- Login `POST /api/auth/login`.
- BCrypt pour les mots de passe.
- Rôles `USER` / `ADMIN` avec authorities `ROLE_USER` / `ROLE_ADMIN`.
- Protection `/api/user/getList` réservée ADMIN.
- Fichier .env ajouté pour gérer les secrets.
- Fichier .env.example poussé pour guider le développeur.
- Tests Postman : **validés**.
- Refresh token : **implémenté**.

---

## Phase 6 - Swagger

- Mise en place de la doc d'api Swagger
- Paramétrage des accès de sécurité dans devSecurityConfig
- Optimisation de la doc existante dans les classes

--- 

## Phase 7 - Mise en place des tests

- Tests sur User
  - Sur le repository avec @DataJpaTest (JPA/H2) -> ACCES DONNES / tests d'intégration repo / JPA
  - Sur le service 
    - avec Mockito -> tests unitaire METIER
    - test SECURITE / authorisations Spring Security
  - Sur le contrôleur avec MockMvc -> endPoint HTTP / JSON / wrapper HttpPostResult  
  - TicketBackApplicationTests -> tests de démarrage du CONTEXTE global

--- 

## Phase 8 - Optimisation

### Centralisation des routes

- `ApiRoutes` : source de vérité unique des chemins d'endpoints en constantes `static final String`, regroupées en classes imbriquées par contrôleur (`Auth` / `Health` / `User` / `Ticket`).
- Contrainte assumée : une valeur d'annotation doit être une constante de compilation → constantes plutôt qu'une `Map` runtime.
- Référencées partout : `@RequestMapping`/`@PostMapping`, tests MockMvc, `requestMatchers` des `SecurityFilterChain` → un renommage de chemin se propage depuis un seul endroit.
- `ROUTES.md` : inventaire des endpoints (format inspiré des fichiers de routes).

### Déconnexion

- Endpoint `POST /api/auth/logout` : révocation du refresh token en base + message de confirmation.
- Front : logout silencieux si forcé (session expirée), avec message si déconnexion explicite.

### Refresh token — durcissement

- Entité `RefreshToken` : on stocke le **hash SHA-256**, jamais le token brut.
- **Rotation** à chaque refresh + double borne : idle glissant **30 min** (`lastUsedAt`), max absolu **8 h** (`createdAt` conservé lors de la rotation).
- Access token ramené à **15 min**.
- Endpoints : `login` (émission), `refresh` (rotation), `logout` (révocation).

### Corrections de robustesse

- `.properties` : suppression des commentaires en fin de ligne (cassaient le binding de `JwtProperties`).
- Tests : chemins recomposés avec la base (`ApiRoutes.User.BASE + ...`).
- `RefreshTokenRepository` : type d'ID corrigé en `Long`.
- `/error` passé en `permitAll` — sinon toute exception d'un endpoint public ressort en **403 trompeur** (forward `/error` bloqué par `anyRequest().authenticated()`).

### Compatibilité Spring Boot 4

- **springdoc-openapi 2.6.0 → 3.0.2** : la 2.x (compilée pour Spring 6) plantait sur Spring Framework 7 (`NoSuchMethodError` sur `ControllerAdviceBean`, déclenché par le `@RestControllerAdvice`). La 3.x supporte Boot 4.
- Java 21+ recommandé pour springdoc 3.x / Boot 4.

--- 

## Phase 9 - Connecter au front (en cours)