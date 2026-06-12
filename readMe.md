# Ticket back-end

## Présentation

Application de ticketing de type Jira ou AzureDevOps, développée en Java SpringBoot.
Elle expose des api REST, avec une couche de sécurité via l'usage de JWT et SpringSecurity, des rôles types qui donnent accès à diverses fonctionalités, et JPA (Hibernate) fournit l'ORM.

## Fonctionnel attendu

### Utilisateurs

- Login / logout
- Rôles: USER, ADMIN
- Accès restreint selon le rôle

### Tickets

- Créer, modifier, supprimer
- Status: OPEN, IN_PROGRESS, DONE
- Assignation à un utilisateur
- Pagination, tri, filtre
- Historique

## Lancement du projet

Installer les dépendances requises puis dans un terminal

````bash
mvn spring-boot:run
````

## Swagger

La documentation des api se trouve a cette adresse: http://localhost:8080/swagger-ui/index.html

## Base de donnée H2 (devMode)

L'interface de l'administration H2 se trouve ici: http://localhost:8080/h2-console/

## Tests

Lancer tous les tests

```bash
mvn clean test
mvn clean verify
```