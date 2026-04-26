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