# Routes / Endpoints — ticket-back

Liste de tous les endpoints exposés par l'API (Spring Boot).
Format inspiré des fichiers `*.routes` du framework maison (Play) : `MÉTHODE  /chemin  Contrôleur.méthode(args)`.

> ⚠️ Fichier de **documentation** uniquement — le routing réel est porté par les annotations
> Spring (`@RequestMapping` / `@GetMapping` / `@PostMapping`) dans les contrôleurs.
> À régénérer manuellement quand un endpoint est ajouté/déplacé.

Convention niveau d'accès (issue des `@Operation.tags`) :
`[PUBLIC]` = ouvert · `[AUTHENTICATED]` = token requis · `[OWNER]` = propriétaire · `[ADMIN]` = rôle admin.

---

### Authentification — `AuthController` (`/api/auth`)
```
POST  /api/auth/login              AuthController.login(LoginRequest)                    # [PUBLIC]
```

### Statut de l'api — `HealthController`
```
GET   /api/health                  HealthController.health()                             # [PUBLIC]
```

### Utilisateurs — `UserController` (`/api/user`)
```
POST   /api/user/get/{id}           UserController.get(id: Long)                         # [AUTHENTICATED]
POST  /api/user/getList            UserController.getList(BaseHttpParams)                # [ADMIN]
POST   /api/user/getUpdate/{id}     UserController.getUpdate(id: Long)                   # [OWNER, ADMIN]
POST  /api/user/update             UserController.update(UserFormDto)                    # [OWNER, ADMIN]
POST   /api/user/metaCreate         UserController.metaCreate()                          # [PUBLIC]
POST  /api/user/create             UserController.create(UserFormDto)                    # [PUBLIC]
POST  /api/user/delete             UserController.delete(id: Long)                       # [OWNER, ADMIN]
```

### Tickets — `TicketController` (`/api/tickets`)
```
POST   /api/tickets                 TicketController.list(status: TicketStatus, Pageable)  # [AUTHENTICATED]
POST  /api/tickets                 TicketController.create(Ticket)                         # [AUTHENTICATED]
```

---

**Total : 11 endpoints** répartis sur 4 contrôleurs.

Documentation interactive complète : **Swagger UI** (via `OpenApiConfig`) — cf. `documentation/OpenApiConfig.java`.
