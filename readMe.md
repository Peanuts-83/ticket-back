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