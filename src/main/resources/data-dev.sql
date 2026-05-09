
-- ============================================================
-- Données DEV TicketFlow
-- Ce fichier est exécuté automatiquement au démarrage du profil dev.
-- Base cible : jdbc:h2:mem:ticketdb
-- ============================================================

-- ------------------------------------------------------------
-- Utilisateurs
-- ------------------------------------------------------------
INSERT INTO app_user (id, username, email, password, role)
VALUES
    (1, 'admin', 'admin@tf.local', 'passWord?1', 'ADMIN'),
    (2, 'thomas', 'thomas@tf.local', 'passWord?1', 'USER'),
    (3, 'marie', 'marie@tf.local', 'passWord?1', 'USER');

-- ------------------------------------------------------------
-- Tickets
-- À adapter selon les colonnes exactes de ton entité Ticket.
-- ------------------------------------------------------------
INSERT INTO tickets (id, title, description, status)
VALUES
    (1, 'Premier ticket de test', 'Ticket créé automatiquement au démarrage dev.', 'NEW'),
    (2, 'Corriger le formulaire de création', 'Vérifier la validation côté front et back.', 'IN_PROGRESS'),
    (3, 'Préparer la connexion Angular', 'Tester les appels API depuis le front.', 'DONE');
