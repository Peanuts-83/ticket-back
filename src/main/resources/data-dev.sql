
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
    (1, 'admin', 'admin@tf.local', '$2a$10$gC2kcw7Bbd/IJ6/NEkPmQOOzLf/ZczrJFnSpCOraumFrRpU.mZGLy', 'ADMIN', CURRENT_TIMESTAMP, 'ACTIVE', null),
    (2, 'thomas', 'thomas@tf.local', '$2a$10$Bz5vk5hAa6SZL8sGKxaEGu6rrpCVqLxGgMZOIDB30kq1i69qWaKe.', 'USER', CURRENT_TIMESTAMP, 'ACTIVE', null),
    (3, 'marie', 'marie@tf.local', '$2a$10$Bz5vk5hAa6SZL8sGKxaEGu6rrpCVqLxGgMZOIDB30kq1i69qWaKe.', 'USER', CURRENT_TIMESTAMP, 'SUSPENDED', null);

-- ------------------------------------------------------------
-- Tickets — étalés sur 30 jours, positions par colonne, quelques clôtures
-- ------------------------------------------------------------
INSERT INTO tickets (id, title, description, status, position, created_at, updated_at, closed_at)
VALUES
    -- NEW
    (1,  'Ajouter le filtre par assigné',      'Filtre sur la liste des tickets.',        'NEW',        0, DATEADD('DAY',  -2, CURRENT_TIMESTAMP), DATEADD('DAY',  -2, CURRENT_TIMESTAMP), NULL),
    (2,  'Export CSV des tickets',             'Export de la vue courante.',              'NEW',        1, DATEADD('DAY',  -4, CURRENT_TIMESTAMP), DATEADD('DAY',  -4, CURRENT_TIMESTAMP), NULL),
    (3,  'Notifications par mail',             'À la création et au changement d''état.', 'NEW',        2, DATEADD('DAY',  -1, CURRENT_TIMESTAMP), DATEADD('DAY',  -1, CURRENT_TIMESTAMP), NULL),
    -- CONCEPTION
    (4,  'Maquette du tableau de bord',        'Camemberts et série 30 jours.',           'CONCEPTION', 0, DATEADD('DAY',  -8, CURRENT_TIMESTAMP), DATEADD('DAY',  -3, CURRENT_TIMESTAMP), NULL),
    (5,  'Modèle de commentaires',             'Entité et relations.',                    'CONCEPTION', 1, DATEADD('DAY', -11, CURRENT_TIMESTAMP), DATEADD('DAY',  -5, CURRENT_TIMESTAMP), NULL),
    (6,  'Choix de la lib de graphes',         'ECharts retenu.',                         'CONCEPTION', 2, DATEADD('DAY',  -6, CURRENT_TIMESTAMP), DATEADD('DAY',  -6, CURRENT_TIMESTAMP), NULL),
    -- ACTIVE
    (7,  'Kanban drag and drop',               'CDK drag-drop, 5 colonnes.',              'ACTIVE',     0, DATEADD('DAY', -14, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP,                      NULL),
    (8,  'Endpoint getStats',                  'Agrégats par statut et par jour.',        'ACTIVE',     1, DATEADD('DAY', -13, CURRENT_TIMESTAMP), DATEADD('DAY',  -1, CURRENT_TIMESTAMP), NULL),
    (9,  'Thème bleu roi',                     'Palette générée + Inter.',                'ACTIVE',     2, DATEADD('DAY', -17, CURRENT_TIMESTAMP), DATEADD('DAY',  -2, CURRENT_TIMESTAMP), NULL),
    (10, 'Responsive mobile du board',         'Une colonne par écran, scroll-snap.',     'ACTIVE',     3, DATEADD('DAY',  -9, CURRENT_TIMESTAMP), DATEADD('DAY',  -1, CURRENT_TIMESTAMP), NULL),
    -- REVIEW
    (11, 'Toggle light/dark persistant',       'localStorage + classe sur html.',         'REVIEW',     0, DATEADD('DAY', -20, CURRENT_TIMESTAMP), DATEADD('DAY',  -4, CURRENT_TIMESTAMP), NULL),
    (12, 'Alignement de la navbar',            'Bloc utilisateur à droite.',              'REVIEW',     1, DATEADD('DAY', -22, CURRENT_TIMESTAMP), DATEADD('DAY',  -3, CURRENT_TIMESTAMP), NULL),
    -- DONE (closed_at renseigné, postérieur à created_at)
    (13, 'Refresh token',                      'Rotation et double borne.',               'DONE',       0, DATEADD('DAY', -28, CURRENT_TIMESTAMP), DATEADD('DAY', -21, CURRENT_TIMESTAMP), DATEADD('DAY', -21, CURRENT_TIMESTAMP)),
    (14, 'Configuration Swagger',              'Documentation auto de l''API.',           'DONE',       1, DATEADD('DAY', -26, CURRENT_TIMESTAMP), DATEADD('DAY', -18, CURRENT_TIMESTAMP), DATEADD('DAY', -18, CURRENT_TIMESTAMP)),
    (15, 'Tests service et controller user',   'MockMvc et H2.',                          'DONE',       2, DATEADD('DAY', -24, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP)),
    (16, 'Intercepteurs HTTP front',           'baseUrl, auth, refresh, erreurs.',        'DONE',       3, DATEADD('DAY', -19, CURRENT_TIMESTAMP), DATEADD('DAY',  -5, CURRENT_TIMESTAMP), DATEADD('DAY',  -5, CURRENT_TIMESTAMP));

-- Repositionne la séquence IDENTITY au-delà des ids insérés à la main
ALTER TABLE tickets ALTER COLUMN id RESTART WITH 100;
ALTER TABLE app_user ALTER COLUMN id RESTART WITH 100;