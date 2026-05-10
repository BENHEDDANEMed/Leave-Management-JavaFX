# Système de Gestion des Congés

Application Java Desktop développée avec JavaFX pour gérer les demandes de congés dans une organisation.

## 📋 Description

Cette application permet de gérer les congés des employés avec trois niveaux d'accès :
- **Employé** : Consulter son solde, soumettre des demandes, suivre leur statut
- **Manager** : Valider/refuser les demandes de son équipe, consulter le planning
- **Admin** : Gérer les utilisateurs, paramétrer les types de congés, ajuster les soldes

## 🛠️ Technologies Utilisées

- **Java 17**
- **JavaFX 21** pour l'interface graphique
- **Maven** pour la gestion des dépendances
- **Architecture MVC** (Model-View-Controller)

## 📁 Structure du Projet

```
src/
├── main/
│   └── java/
│       └── com/
│           └── conges/
│               ├── App.java                    # Point d'entrée
│               ├── model/                      # Modèle de données
│               │   ├── Utilisateur.java
│               │   ├── DemandeConge.java
│               │   ├── SoldeConge.java
│               │   ├── TypeConge.java
│               │   ├── Role.java
│               │   └── StatutDemande.java
│               ├── dao/                        # Accès aux données
│               │   ├── UtilisateurDAO.java
│               │   ├── DemandeCongeDAO.java
│               │   ├── SoldeCongeDAO.java
│               │   └── TypeCongeDAO.java
│               ├── service/                    # Logique métier
│               │   ├── CongeService.java
│               │   └── SessionManager.java
│               ├── view/                       # Interface utilisateur
│               │   ├── LoginView.java
│               │   ├── EmployeView.java
│               │   ├── ManagerView.java
│               │   └── AdminView.java
│               └── controller/                 # Contrôleurs (à venir)
└── test/
    └── java/
        └── com/
            └── conges/
                └── AppTest.java
```

## 🚀 Installation et Exécution

### Prérequis

- Java Development Kit (JDK) 17 ou supérieur
- Maven 3.6 ou supérieur

### Installation

1. Cloner ou télécharger le projet
2. Ouvrir le projet dans VS Code
3. Compiler le projet :
   ```bash
   mvn clean compile
   ```

### Exécution

Depuis VS Code, vous pouvez exécuter l'application de plusieurs façons :

**Option 1 : Via Maven**
```bash
mvn javafx:run
```

**Option 2 : Via la classe principale**
- Ouvrir `App.java`
- Cliquer sur "Run" au-dessus de la méthode `main`

**Option 3 : Via le terminal**
```bash
mvn clean javafx:run
```

## 👤 Comptes de Test

L'application est initialisée avec des comptes de test :

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| **Admin** | admin@conges.com | admin123 |
| **Manager** | ahmed.alami@conges.com | manager123 |
| **Employé** | fatima.bennani@conges.com | emp123 |
| **Employé** | karim.elidrissi@conges.com | emp123 |

## 📊 Fonctionnalités

### Employé
- ✅ Consulter son solde de congés
- ✅ Soumettre une nouvelle demande de congé
- ✅ Suivre l'état de ses demandes
- ✅ Voir l'historique des congés

### Manager
- ✅ Visualiser les demandes de l'équipe
- ✅ Approuver ou refuser les demandes
- ✅ Consulter les membres de l'équipe
- 🔄 Planning d'équipe (à venir)

### Administrateur
- ✅ Gérer les comptes utilisateurs
- ✅ Créer de nouveaux utilisateurs
- ✅ Paramétrer les types de congés
- ✅ Ajuster les soldes annuels
- 🔄 Statistiques globales (à venir)

## 💾 Base de Données

Actuellement, l'application utilise un stockage **en mémoire** avec des listes Java.

### Évolution future : Intégration avec une base de données

Pour passer à une base de données réelle (MySQL, PostgreSQL, etc.), il faudra :

1. Ajouter les dépendances JDBC dans `pom.xml`
2. Créer les tables SQL correspondantes
3. Modifier les classes DAO pour utiliser JDBC au lieu des listes

Exemple de structure SQL :
```sql
CREATE TABLE utilisateurs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    email VARCHAR(255) UNIQUE,
    mot_de_passe VARCHAR(255),
    role ENUM('EMPLOYE', 'MANAGER', 'ADMIN'),
    departement VARCHAR(100),
    date_embauche DATE,
    id_manager INT,
    actif BOOLEAN DEFAULT TRUE
);

-- Autres tables : types_conge, demandes_conge, soldes_conge
```

## 🎨 Interface Utilisateur

L'interface est développée avec JavaFX et utilise :
- Layout responsive avec BorderPane, VBox, HBox
- Styles CSS inline pour un design moderne
- Tableaux interactifs (TableView)
- Formulaires avec validation
- Navigation par menu latéral

## 🔄 Améliorations Futures

- [ ] Intégration base de données (MySQL/PostgreSQL)
- [ ] Système de notifications
- [ ] Export des données (PDF, Excel)
- [ ] Planning visuel avec calendrier
- [ ] Rapports et statistiques avancés
- [ ] Gestion des jours fériés
- [ ] Validation des congés sur plusieurs niveaux
- [ ] API REST pour application mobile

## 📝 Notes de Développement

### Architecture

Le projet suit le pattern **MVC** :
- **Model** : Classes de données pures (POJO)
- **View** : Interfaces JavaFX
- **Controller** : Actuellement intégré dans les vues (à séparer pour une meilleure modularité)

### Pattern DAO

Les classes DAO (Data Access Object) encapsulent l'accès aux données, facilitant le remplacement du stockage en mémoire par une base de données réelle.

### Singleton

Les DAOs et les services utilisent le pattern Singleton pour garantir une seule instance en mémoire.

## 📞 Support

Pour toute question ou suggestion, contactez l'équipe de développement.

## 📄 Licence

Projet éducatif - Ecole Marocaine des Sciences de l'Ingénieur
