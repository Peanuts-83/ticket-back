# TicketFlow Back — État actuel du projet Java / Spring Boot

> Document de contexte

---

## 1. Objectif du back

Le back correspond à l’API Java de l’application **TicketFlow**, une application de gestion de tickets de type **Jira simplifié**.

Objectifs techniques principaux :

- Exposer des APIs REST homogènes pour le front Angular.
- Utiliser Spring Boot, Spring Web, Spring Security, Spring Data JPA et Lombok.
- Préparer une architecture maintenable : controller / service / repository / domain / security.
- Gérer à terme l’authentification JWT.
- Permettre le développement avec H2 et la production avec PostgreSQL.
- Standardiser les payloads d’entrée / sortie pour faciliter les services génériques côté front.

---

## 2. Stack back actuelle

- Langage : **Java**
- Framework : **Spring Boot**
- Build : **Maven**
- ORM / persistance : **JPA / Hibernate**
- Réduction boilerplate : **Lombok**
- Sécurité : **Spring Security** en cours de construction
- Base de données dev : **H2**
- Base de données cible prod : **PostgreSQL**

Point important déjà clarifié :

- Avec Spring Boot 3.x, les annotations JPA utilisent le namespace :

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
```

---

## 3. Structure actuelle du projet

Package racine :

```text
com.example.ticketback
```

Le lanceur Spring Boot se trouve à la racine :

```text
com.example.ticketback.TicketBackApplication
```

Structure actuelle :

```text
src/main/java/com/example/ticketback/
├── TicketBackApplication.java
│
├── controller/
│
├── domain/
│   └── entity/
│   └── enums/
│
├── dto/
│   └── common/
│   └── user/
│   └── ticket/
│
├── service/
│
├── repository/
│
└── security/
    └── classe(s) de configuration sécurité
```

À noter :

- Les packages sont sous `com.example.ticketback`, donc Spring Boot scanne automatiquement les composants, services, repositories et entités situés dans ces sous-packages.
- Pas besoin de `@EntityScan` tant que les entités restent sous ce package racine.

---

## 4. Architecture cible côté back

Architecture classique visée :

```text
controller
 └── reçoit les requêtes REST

service
 └── porte la logique métier

repository
 └── accès aux données via Spring Data JPA

domain
 └── entités JPA

dto / model
 └── objets de requête / réponse API

security
 └── configuration Spring Security, JWT, filterChain

exception
 └── gestion centralisée des erreurs, à prévoir
```

Principe important :

> Le controller doit rester fin. Il délègue au service. Le service orchestre la logique métier. Le repository reste dédié à la persistance.

---

## 5. Convention de constructeurs et injection

Bonne pratique retenue :

- Injection par constructeur.
- Un seul constructeur par service/controller dans la majorité des cas.
- Champs `final` pour les dépendances.
- C'est le constructeur qui construit HttpPostResult avec le retour des services (DTO).

Exemple attendu :

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

Avec Lombok, alternative possible :

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
}
```

---

## 6. Base de données

### Stratégie retenue

Objectif final :

- **H2 en dev**
- **PostgreSQL en prod**

Via profils Spring :

```text
application.properties
application-dev.properties
application-prod.properties
```

### Dev — H2

Pour le développement local, la stratégie retenue est :

```text
H2 mémoire + ddl-auto=create-drop + data-dev.sql
```

Objectif : obtenir une base propre et préremplie à chaque démarrage complet de l'application.

- Avec `jdbc:h2:mem:ticketdb`, la base est créée en mémoire au démarrage de l’application.
- Elle disparaît à l’arrêt de l’application.
- La base H2 mémoire `ticketdb` est créée au démarrage.
- Hibernate crée les tables détectées depuis les entités JPA
- La console H2 est accessible via :

```text
http://localhost:8080/h2-console
```

- La connexion H2 doit utiliser exactement :

```text
JDBC URL: jdbc:h2:mem:ticketdb
User Name: sa
Password:
```

### Données de développement

Un script SQL de remplissage automatique est utilisé en profil `dev` :

```text
src/main/resources/data-dev.sql
```

Ce script est exécuté automatiquement au lancement grâce à :

```properties
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data-dev.sql
spring.jpa.defer-datasource-initialization=true
```

La propriété :

```properties
spring.jpa.defer-datasource-initialization=true
```

est importante car elle force l'exécution du script de données après la création du schéma par Hibernate.
### Prod — PostgreSQL

Configuration cible conceptuelle :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketdb
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

À terme, le schéma devrait être géré via Flyway ou Liquibase plutôt que par `ddl-auto=update`.

---

## 7. Stratégie API REST terminale attendue

Standardisation des endpoints terminaux sur le modèle suivant.

Pour une ressource donnée, par exemple `user`, la base d’URL serait :

```text
/api/user
```

### Endpoints standards

```http
GET  /api/user/get/{id}
POST /api/user/getList
GET  /api/user/getUpdate/{id}
POST /api/user/update
GET  /api/user/metaCreate
POST /api/user/create
DELETE /api/user/delete
```

### Endpoints contextuels si besoin

```http
POST /api/user/getListFor/{id}
GET  /api/user/metaCreateFor/{id}
```

### Règle importante

Il ne doit pas y avoir d’`id` dans les routes `update` et `create`.

Les données nécessaires doivent être dans le payload, notamment dans `payload.data`.

Donc :

```http
POST /api/user/update
POST /api/user/create
```

et non :

```http
PUT /api/user/update/{id}
POST /api/user/create/{id}
```

### Exemple fonctionnel souhaité pour créer un user

Étape 1 — récupérer les champs nécessaires :

```http
GET /api/user/metaCreate
```

Étape 2 — enregistrer les données :

```http
POST /api/user/create
```

#### Sémantique actuelle de la réponse API

Le wrapper générique `HttpPostResult<T>` contient désormais :

```java
T data;                     // valeurs des champs
Map<String, Meta> metas;    // métadonnées techniques de chaque champ
Long nb;                    // nombre de lignes remontées dans le bean pour les listes
```

---

## 8. Remarque sur le style REST

Le modèle choisi n’est pas un REST pur classique du type :

```http
GET    /api/user/{id}
GET    /api/user
POST   /api/user
PUT    /api/user/{id}
DELETE /api/user/{id}
```

Il s’agit plutôt d’un style **API applicative / action-based**, proche de certains back-office d’entreprise.

Décision actuelle :

> Assumer ce modèle car il facilite la standardisation côté front et l’usage de services génériques.

Point d’attention :

- Bien documenter cette convention.
- Être cohérent sur toutes les ressources.
- Éviter de mélanger ce modèle avec du REST pur dans le même projet.

---

## 9. Modèles front attendus pour les appels API

Ces modèles viennent de la réflexion côté front, mais ils influencent directement le contrat back.

### Params pour les appels de liste

```ts
/** Params pour les appels de liste */
export interface BaseHttpParamList {
    /** Numero de la page */
    pageNum?: number

    /** Nombre d'éléments par appel - 30 par défaut */
    nb?: number
}
```

### Params de base des appels API

```ts
/** Params de base des appels api */
export interface BaseHttpParams {
    /** Clée utilisée ex: id_maTable */
    id: string,

    /** Type de bean demandé */
    dataType: ViewDataType

    /** param pour getListFor / metaCreateFor */
    routeParam?: number

    /** pour les ecrans liste */
    paramList?: BaseHttpParamList
}
```

### Type de vue / contexte demandé

```ts
export enum ViewDataType {
    LISTE = "liste",
    UPDATE = "update",
    CREATE = "create",
}
```

### Résultat HTTP générique

```ts
export interface HttpPostResult<T> {
    /** données du résultat */
    data: T,

    /** Nbre de ligne du résultat */
    nb?: number
}
```

---

## 10. Traduction côté Java

### Enum `ViewDataType`

```java
public enum ViewDataType {
    TYPE_SELECT,
    LISTE,
    UPDATE,
    CREATE
}
```

À vérifier :

- Si le front envoie `liste`, `update`, `create` en minuscules, il faudra gérer le mapping JSON.
- Option possible : utiliser `@JsonValue` et `@JsonCreator` pour conserver les valeurs minuscules.

Exemple possible :

```java
public enum ViewDataType {
    LISTE("liste"),
    UPDATE("update"),
    CREATE("create");

    private final String value;

    ViewDataType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ViewDataType fromValue(String value) {
        for (ViewDataType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ViewDataType: " + value);
    }
}
```

### BaseHttpParamList Java

```java
public record BaseHttpParamList(
        Integer pageNum,
        Integer nb
) {
    public int resolvedPageNum() {
        return pageNum != null ? pageNum : 0;
    }

    public int resolvedNb() {
        return nb != null ? nb : 30;
    }
}
```

### BaseHttpParams Java

```java
public record BaseHttpParams(
        String id,
        ViewDataType dataType,
        Long routeParam,
        BaseHttpParamList paramList
) {
}
```

### HttpPostResult Java

```java
public record HttpPostResult<T>(
        T data,
        Long nb
) {
    public static <T> HttpPostResult<T> of(T data) {
        return new HttpPostResult<>(data, null);
    }

    public static <T> HttpPostResult<T> of(T data, Long nb) {
        return new HttpPostResult<>(data, nb);
    }
}
```

### Le contrat API générique repose sur les classes contenues dans :

```text
src/main/java/com/example/ticketback/dto/common/
```

Un metaBuilder permet de générer automatiquement les métadonnées techniques de chaque champ en se basant sur le DTO fourni. 

Une annotation @MetaField permet de compléter les métadonnées.

---

## 11. Payloads `create` et `update`

La règle métier actuelle est :

> Pas d’id dans l’URL pour `create` et `update`. Les informations sont dans `payload.data`.

Il faudra donc probablement prévoir un wrapper générique de requête POST.

Exemple côté TypeScript possible :

```ts
export interface HttpPostPayload<T> {
  params?: BaseHttpParams
  data: T
}
```

Équivalent Java possible :

```java
public record HttpPostPayload<T>(
        BaseHttpParams params,
        T data
) {
}
```

Exemple création user :

```json
{
  "data": {
    "username": "admin",
    "email": "admin@ticketflow.local",
    "password": "password",
    "role": "ROLE_ADMIN"
  }
}
```

Exemple update user :

```json
{
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@ticketflow.local"
  }
}
```

À noter :

- Pour `update`, l’id métier doit être dans `data`.
- Pour `create`, l’id peut être absent ou null.

---

## 12. Exemple cible de controller User

Exemple conceptuel aligné avec les conventions souhaitées :

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get/{id}")
    public HttpPostResult<UserDto> get(@PathVariable Long id) {
        return HttpPostResult.of(userService.get(id));
    }

    @PostMapping("/getList")
    public HttpPostResult<List<UserListDto>> getList(@RequestBody(required = false) BaseHttpParams params) {
        return userService.getList(params);
    }

    @GetMapping("/getUpdate/{id}")
    public HttpPostResult<UserUpdateDto> getUpdate(@PathVariable Long id) {
        return HttpPostResult.of(userService.getUpdate(id));
    }

    @PostMapping("/update")
    public HttpPostResult<UserDto> update(@RequestBody HttpPostPayload<UserUpdateDto> payload) {
        return HttpPostResult.of(userService.update(payload.data()));
    }

    @GetMapping("/metaCreate")
    public HttpPostResult<UserCreateMetaDto> metaCreate() {
        return HttpPostResult.of(userService.metaCreate());
    }

    @PostMapping("/create")
    public HttpPostResult<UserDto> create(@RequestBody HttpPostPayload<UserCreateDto> payload) {
        return HttpPostResult.of(userService.create(payload.data()));
    }

    @PostMapping("/delete")
    public HttpPostResult<Long> delete(@RequestBody Long id) {
        return HttpPostResult.of(userService.delete(id));
    }
}
```

Important :

- Exemple non forcément déjà implémenté.
- Sert de cible d’architecture.
- Les DTOs restent à définir.

---

## 13. Authentification / sécurité

### État actuel

Une configuration Spring Security minimale fonctionne en développement.

Fonctionnalités validées :

- application démarrable avec Spring Security actif ;
- endpoint `/api/health` accessible publiquement ;
- console H2 accessible publiquement en dev ;
- CORS fonctionnel pour Angular local ;
- preflight `OPTIONS` autorisé ;
- Validation d'accès avec @EnableMethodSecurity en config et @PreAuthorize dans le service;
- configuration stateless préparée pour JWT.

### TODO principal

La sécurité complète n'est pas encore finalisée.

Prochaine étape prévue :

```text
Authentification locale username/password -> génération accessToken JWT -> validation Bearer token
```


### Endpoints publics

```http
POST /api/auth/login
GET  /api/user/metaCreate
POST /api/user/create
```


### Endpoints privés (authenticated OWNER ou ADMIN)

```http
POST /api/admin/**              # réservé ADMIN
POST /api/user/getList          # réservé ADMIN
GET  /api/user/getUpdate/:id
POST /api/user/update
DELETE /api/user/delete/:id
```

---

## 14. Stratégie JWT à clarifier avec le front

Réflexion actuelle côté front :

> Le front ne devrait probablement connaître que :
>
> - `accessToken`
> - `user` connecté
>
> Le `refreshToken`, s’il existe, devrait rester côté back uniquement.

Conséquences côté back :

- Option recommandée : refresh token en cookie HttpOnly si refresh token nécessaire.
- Le front ne manipule pas directement le refresh token.
- Le front envoie l’access token via :

```http
Authorization: Bearer <accessToken>
```

- Le back valide l’access token dans un filtre JWT.

À décider :

- Y aura-t-il un refresh token ?
- Si oui, sera-t-il en cookie HttpOnly ?
- Le endpoint `/api/auth/refresh` sera-t-il nécessaire ?
- Comment gérer `/api/auth/logout` sans refresh token dans le body ?

---

## 15. Proposition de filterChain cible

Exemple conceptuel :

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```

2 Configurations distinctes sont implémentées: DEV et PROD.

---

## 16. CORS

Les appels navigateur depuis Angular local vers le back Spring Boot sont débloqués.

Origines autorisées en développement :

```text
http://localhost:4200
http://127.0.0.1:4200
```

Méthodes autorisées :

```text
GET
POST
DELETE
OPTIONS
```

Headers autorisés :

```text
Authorization
Content-Type
Accept
Origin
X-Requested-With
```

Configuration CORS à prévoir côté Spring Security / WebMvc :

- Si cookies HttpOnly pour refresh token : gérer `allowCredentials(true)` et SameSite côté cookie.

---

## 17. Services et bases abstraites côté back

Cible possible :

```text
service/
├── BaseCrudService.java
├── BaseMetaService.java
└── UserService.java
```

Ou structure plus simple au départ :

```text
service/
└── UserService.java
```

Point d’attention :

- Ne pas sur-abstraire trop tôt.
- Commencer par `UserService`, puis extraire une classe abstraite quand `TicketService` aura les mêmes patterns.

---

## 18. DTOs à prévoir

Pour éviter d’exposer directement les entités JPA au front, prévoir des DTOs.

FAIT.

## 19. Feature tickets côté back

À venir.

Ressource cible :

```text
/api/ticket
```

Endpoints selon convention :

```http
GET  /api/ticket/get/{id}
POST /api/ticket/getList
GET  /api/ticket/getUpdate/{id}
POST /api/ticket/update
GET  /api/ticket/metaCreate
POST /api/ticket/create
POST /api/ticket/getListFor/{id}
GET  /api/ticket/metaCreateFor/{id}
```

Fonctionnalités futures :

- Création ticket.
- Liste paginée.
- Détail ticket.
- Modification ticket.
- Statuts.
- Priorités.
- Assignation user.
- Historique / commentaires plus tard.

---

## 20. TODO priorisés back

### TODO 1 — Faire démarrer proprement le back - DONE &#x2714; 

- Vérifier Maven.
- Vérifier version Spring Boot 3.x.
- Vérifier dépendances : web, data-jpa, security, validation, h2, postgresql, lombok.
- Vérifier imports `jakarta.persistence`.
- Vérifier profils H2/PostgreSQL.

### TODO 2 — Stabiliser le contrat API générique - DONE &#x2714;

- `BaseHttpParams`
- `BaseHttpParamList`
- `ViewDataType`
- `HttpPostResult<T>`
- éventuellement `HttpPostPayload<T>`

### TODO 3 — Implémenter User minimal - DONE &#x2714;

- Entity `User`.
- Repository `UserRepository`.
- Service `UserService`.
- Controller `UserController` avec endpoints conventionnés.
- DTOs minimaux.

### TODO 4 — Avancer sur security / filterChain - DONE &#x2714;

- `SecurityFilterChain`.
- Endpoints publics.
- CORS.
- H2 console en dev.
- Préparation JWT.

### TODO 5 — Préparer JWT

- Service de génération access token.
- Filtre de validation JWT.
- `Authorization: Bearer <accessToken>`.
- Décider refresh token ou pas côté front.

### TODO 6 — Documentation

- Mettre en place une documentation auto type Swagger

### TODO 7 — Connecter le front

- Vérifier CORS.
- Aligner les URLs avec le `baseUrl interceptor` front.
- Tester login/register ou user/create selon stratégie.

### TODO 8 — Tests back

À prévoir :

- Tests service.
- Tests repository avec H2.
- Tests controller avec MockMvc.
- Tests security pour endpoints publics/protégés.

---

## 21. Décisions techniques actuelles

| Sujet | Décision / orientation actuelle |
|---|---|
| Package racine | `com.example.ticketback` |
| Launcher | `TicketBackApplication` à la racine |
| Architecture | controller / service / repository / domain / security |
| ORM | JPA / Hibernate |
| DB dev | H2 |
| DB prod | PostgreSQL |
| Endpoints | modèle action-based : get, getList, getUpdate, update, metaCreate, create |
| Update/create | pas d’id dans l’URL, données dans `payload.data` |
| Réponse API | `HttpPostResult<T>` |
| Pagination | `BaseHttpParamList` avec `pageNum`, `nb` |
| Auth | Spring Security + JWT à avancer |
| Refresh token | à clarifier, probablement pas exposé au front |
| Tests | à prévoir |

---

