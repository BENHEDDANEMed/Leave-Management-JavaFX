-- Base de données pour le système de gestion des congés
-- À exécuter dans phpMyAdmin (XAMPP)

CREATE DATABASE IF NOT EXISTS gestion_conges CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE gestion_conges;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('EMPLOYE', 'MANAGER', 'ADMIN') NOT NULL,
    departement VARCHAR(100),
    date_embauche DATE NOT NULL,
    id_manager INT,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_manager) REFERENCES utilisateurs(id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_manager (id_manager)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des types de congés
CREATE TABLE IF NOT EXISTS types_conge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    jours_max_par_an INT NOT NULL,
    est_paye BOOLEAN DEFAULT TRUE,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des soldes de congés
CREATE TABLE IF NOT EXISTS soldes_conge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_type_conge INT NOT NULL,
    annee INT NOT NULL,
    jours_alloues DECIMAL(5,2) NOT NULL DEFAULT 0,
    jours_utilises DECIMAL(5,2) NOT NULL DEFAULT 0,
    jours_en_attente DECIMAL(5,2) NOT NULL DEFAULT 0,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    FOREIGN KEY (id_type_conge) REFERENCES types_conge(id) ON DELETE CASCADE,
    UNIQUE KEY unique_solde (id_utilisateur, id_type_conge, annee),
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_annee (annee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des demandes de congés
CREATE TABLE IF NOT EXISTS demandes_conge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT NOT NULL,
    id_type_conge INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    nombre_jours DECIMAL(5,2) NOT NULL,
    motif TEXT,
    statut ENUM('EN_ATTENTE', 'APPROUVEE', 'REFUSEE', 'ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_validateur INT,
    date_validation TIMESTAMP NULL,
    commentaire_validation TEXT,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    FOREIGN KEY (id_type_conge) REFERENCES types_conge(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_validateur) REFERENCES utilisateurs(id) ON DELETE SET NULL,
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_statut (statut),
    INDEX idx_dates (date_debut, date_fin),
    INDEX idx_validateur (id_validateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion des données de test

-- Utilisateurs
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, departement, date_embauche, id_manager) VALUES
('Admin', 'Système', 'admin@conges.com', 'admin123', 'ADMIN', 'Administration', '2020-01-01', NULL),
('Alami', 'Ahmed', 'ahmed.alami@conges.com', 'manager123', 'MANAGER', 'Informatique', '2021-03-15', NULL),
('Bennani', 'Fatima', 'fatima.bennani@conges.com', 'emp123', 'EMPLOYE', 'Informatique', '2022-06-01', 2),
('El Idrissi', 'Karim', 'karim.elidrissi@conges.com', 'emp123', 'EMPLOYE', 'Informatique', '2023-01-10', 2);

-- Types de congés
INSERT INTO types_conge (nom, description, jours_max_par_an, est_paye) VALUES
('Congé annuel', 'Congé payé annuel', 22, TRUE),
('Congé maladie', 'Congé pour raison de santé', 90, TRUE),
('Congé sans solde', 'Congé non payé', 30, FALSE),
('Congé maternité', 'Congé de maternité', 98, TRUE),
('Congé paternité', 'Congé de paternité', 3, TRUE);

-- Soldes pour Mohamed (ID 5)
INSERT INTO soldes_conge (id_utilisateur, id_type_conge, annee, jours_alloues, jours_utilises, jours_en_attente) VALUES
(5, 1, 2025, 22, 0, 0),
(5, 2, 2025, 90, 0, 0);

-- Soldes pour Hamza (ID 6)
INSERT INTO soldes_conge (id_utilisateur, id_type_conge, annee, jours_alloues, jours_utilises, jours_en_attente) VALUES
(6, 1, 2025, 22, 0, 0),
(6, 2, 2025, 90, 0, 0);

-- Demandes de congés
INSERT INTO demandes_conge (id_utilisateur, id_type_conge, date_debut, date_fin, nombre_jours, motif, statut, id_validateur, date_validation) VALUES
(3, 1, '2025-12-20', '2025-12-24', 5, 'Vacances de fin d''année', 'APPROUVEE', 2, '2025-12-10 10:30:00'),
(4, 1, '2026-01-15', '2026-01-17', 3, 'Congé personnel', 'EN_ATTENTE', NULL, NULL);


-- Assigner Mohamed (ID 5) au manager Ayoub (ID 7)
UPDATE utilisateurs SET id_manager = 7 WHERE id = 5;

-- Assigner Hamza (ID 6) au manager Ayoub (ID 7)
UPDATE utilisateurs SET id_manager = 7 WHERE id = 6;