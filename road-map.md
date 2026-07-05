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
- Refresh token : **non implémenté**, à décider plus tard.

---

## Phase 6 - Swagger

- Mise en place de la doc d'api Swagger
- Paramétrage des accès de sécurité dans devSecurityConfig
- Optimisation de la doc existante dans les classes

--- 

## Phase 7 - Mise en place des tests (à compléter)

### DONE 

- Tests sur User
  - Sur le repository avec @DataJpaTest (JPA/H2) -> ACCES DONNES / tests d'intégration repo / JPA
  - Sur le service 
    - avec Mockito -> tests unitaire METIER
    - test SECURITE / authorisations Spring Security
  - Sur le contrôleur avec MockMvc -> endPoint HTTP / JSON / wrapper HttpPostResult  
  - TicketBackApplicationTests -> tests de démarrage du CONTEXTE global

--- 

## Phase 8 - Optimisation (en attente)

--- 

## Phase 9 - Connecter au front (en cours)