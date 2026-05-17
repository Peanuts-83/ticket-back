
-- ============================================================
-- Données DEV TicketFlow
-- Ce fichier est exécuté automatiquement au démarrage du profil dev.
-- Base cible : jdbc:h2:mem:ticketdb
-- ============================================================

-- ------------------------------------------------------------
-- Utilisateurs
-- ------------------------------------------------------------
INSERT INTO app_user (id, username, email, password, role, dt_created, status, avatar)
VALUES
    (1, 'admin', 'admin@tf.local', '$2a$10$Bz5vk5hAa6SZL8sGKxaEGu6rrpCVqLxGgMZOIDB30kq1i69qWaKe.', 'ADMIN', CURRENT_TIMESTAMP, 'ACTIVE', null),
    (2, 'thomas', 'thomas@tf.local', '$2a$10$Bz5vk5hAa6SZL8sGKxaEGu6rrpCVqLxGgMZOIDB30kq1i69qWaKe.', 'USER', CURRENT_TIMESTAMP, 'ACTIVE', null),
    (3, 'marie', 'marie@tf.local', '$2a$10$Bz5vk5hAa6SZL8sGKxaEGu6rrpCVqLxGgMZOIDB30kq1i69qWaKe.', 'USER', CURRENT_TIMESTAMP, 'SUSPENDED', null);

-- ------------------------------------------------------------
-- Tickets
-- À adapter selon les colonnes exactes de ton entité Ticket.
-- ------------------------------------------------------------
INSERT INTO tickets (id, title, description, status)
VALUES
    (1, 'Premier ticket de test', 'Ticket créé automatiquement au démarrage dev.', 'NEW'),
    (2, 'Corriger le formulaire de création', 'Vérifier la validation côté front et back.', 'IN_PROGRESS'),
    (3, 'Préparer la connexion Angular', 'Tester les appels API depuis le front.', 'DONE');
