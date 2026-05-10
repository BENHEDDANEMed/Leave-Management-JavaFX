package com.conges.view;

import java.time.LocalDate;
import java.util.List;

import com.conges.dao.SoldeCongeDAO;
import com.conges.dao.TypeCongeDAO;
import com.conges.dao.UtilisateurDAO;
import com.conges.model.Role;
import com.conges.model.SoldeConge;
import com.conges.model.TypeConge;
import com.conges.model.Utilisateur;
import com.conges.service.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Vue du tableau de bord administrateur
 */
public class AdminView {
    private Stage stage;
    private Utilisateur utilisateur;
    private UtilisateurDAO utilisateurDAO;
    private TypeCongeDAO typeCongeDAO;
    private SoldeCongeDAO soldeCongeDAO;
    
    public AdminView() {
        this.utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        this.utilisateurDAO = UtilisateurDAO.getInstance();
        this.typeCongeDAO = TypeCongeDAO.getInstance();
        this.soldeCongeDAO = SoldeCongeDAO.getInstance();
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        stage.setTitle("Tableau de bord - Administrateur");
        stage.setWidth(1100);
        stage.setHeight(700);
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ecf0f1;");
        
        // En-tête
        root.setTop(creerEntete());
        
        // Menu à gauche
        root.setLeft(creerMenu());
        
        // Contenu principal
        root.setCenter(creerContenuAccueil());
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox creerEntete() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: #8e44ad; -fx-padding: 20;");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);
        
        Label titleLabel = new Label("Espace Administrateur");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("👤 " + utilisateur.getNomComplet());
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setTextFill(Color.WHITE);
        
        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> deconnecter());
        
        headerContent.getChildren().addAll(titleLabel, spacer, userLabel, logoutBtn);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private VBox creerMenu() {
        VBox menu = new VBox(10);
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: #9b59b6; -fx-padding: 20;");
        
        Button btnAccueil = creerBoutonMenu("🏠 Accueil");
        btnAccueil.setOnAction(e -> afficherAccueil());
        
        Button btnUtilisateurs = creerBoutonMenu("👥 Gérer utilisateurs");
        btnUtilisateurs.setOnAction(e -> afficherUtilisateurs());
        
        Button btnTypesConges = creerBoutonMenu("📝 Types de congés");
        btnTypesConges.setOnAction(e -> afficherTypesConges());
        
        Button btnSoldes = creerBoutonMenu("💰 Ajuster soldes");
        btnSoldes.setOnAction(e -> afficherSoldes());
        
        menu.getChildren().addAll(btnAccueil, btnUtilisateurs, btnTypesConges, btnSoldes);
        
        return menu;
    }
    
    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setPrefWidth(180);
        btn.setPrefHeight(40);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> 
            btn.setStyle(
                "-fx-background-color: #8e44ad;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;"
            )
        );
        btn.setOnMouseExited(e -> 
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;"
            )
        );
        return btn;
    }
    
    private VBox creerContenuAccueil() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label welcomeLabel = new Label("Bienvenue, Administrateur !");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.web("#2c3e50"));
        
        // Cartes de statistiques
        HBox statsBox = new HBox(20);
        
        List<Utilisateur> utilisateurs = utilisateurDAO.getTous();
        List<TypeConge> typesConges = typeCongeDAO.getTous();
        
        long nbEmployes = utilisateurs.stream()
            .filter(u -> u.getRole() == Role.EMPLOYE)
            .count();
        
        VBox card1 = creerCarte("Total utilisateurs", String.valueOf(utilisateurs.size()), "#3498db");
        VBox card2 = creerCarte("Employés", String.valueOf(nbEmployes), "#2ecc71");
        VBox card3 = creerCarte("Types de congés", String.valueOf(typesConges.size()), "#e67e22");
        
        statsBox.getChildren().addAll(card1, card2, card3);
        
        // Vue d'ensemble
        Label overviewTitle = new Label("Vue d'ensemble du système");
        overviewTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        VBox overviewBox = new VBox(10);
        overviewBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 20;" +
            "-fx-background-radius: 10;"
        );
        
        Label info1 = new Label("• Système opérationnel");
        Label info2 = new Label("• " + utilisateurs.size() + " utilisateurs actifs");
        Label info3 = new Label("• " + typesConges.size() + " types de congés configurés");
        
        overviewBox.getChildren().addAll(info1, info2, info3);
        
        content.getChildren().addAll(welcomeLabel, statsBox, overviewTitle, overviewBox);
        
        return content;
    }
    
    private VBox creerCarte(String titre, String valeur, String couleur) {
        VBox card = new VBox(10);
        card.setPrefSize(200, 100);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);" +
            "-fx-padding: 20;"
        );
        
        Label valeurLabel = new Label(valeur);
        valeurLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        valeurLabel.setTextFill(Color.web(couleur));
        
        Label titreLabel = new Label(titre);
        titreLabel.setFont(Font.font("Arial", 12));
        titreLabel.setTextFill(Color.web("#7f8c8d"));
        
        card.getChildren().addAll(valeurLabel, titreLabel);
        
        return card;
    }
    
    private void afficherAccueil() {
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(creerContenuAccueil());
    }
    
    private void afficherUtilisateurs() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Gestion des utilisateurs");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button ajouterBtn = new Button("➕ Nouvel utilisateur");
        ajouterBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;"
        );
        ajouterBtn.setOnAction(e -> afficherDialogueNouvelUtilisateur());
        
        titleBox.getChildren().addAll(titleLabel, spacer, ajouterBtn);
        
        // Tableau des utilisateurs
        TableView<Utilisateur> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");
        
        TableColumn<Utilisateur, String> nomCol = new TableColumn<>("Nom complet");
        nomCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomComplet())
        );
        nomCol.setPrefWidth(180);
        
        TableColumn<Utilisateur, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(220);
        
        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRole().getLibelle())
        );
        roleCol.setPrefWidth(120);
        
        TableColumn<Utilisateur, String> deptCol = new TableColumn<>("Département");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("departement"));
        deptCol.setPrefWidth(150);
        
        TableColumn<Utilisateur, Boolean> actifCol = new TableColumn<>("Actif");
        actifCol.setCellValueFactory(new PropertyValueFactory<>("actif"));
        actifCol.setPrefWidth(80);
        
        table.getColumns().addAll(nomCol, emailCol, roleCol, deptCol, actifCol);
        
        // Charger les données
        List<Utilisateur> utilisateurs = utilisateurDAO.getTous();
        table.setItems(FXCollections.observableArrayList(utilisateurs));
        
        content.getChildren().addAll(titleBox, table);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherDialogueNouvelUtilisateur() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nouvel utilisateur");
        dialog.setWidth(450);
        dialog.setHeight(550);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Créer un nouvel utilisateur");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        roleCombo.setValue(Role.EMPLOYE);
        
        TextField deptField = new TextField();
        deptField.setPromptText("Département");
        
        DatePicker dateEmbauche = new DatePicker();
        dateEmbauche.setValue(LocalDate.now());
        
        form.add(new Label("Nom:"), 0, 0);
        form.add(nomField, 1, 0);
        form.add(new Label("Prénom:"), 0, 1);
        form.add(prenomField, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Mot de passe:"), 0, 3);
        form.add(passwordField, 1, 3);
        form.add(new Label("Rôle:"), 0, 4);
        form.add(roleCombo, 1, 4);
        form.add(new Label("Département:"), 0, 5);
        form.add(deptField, 1, 5);
        form.add(new Label("Date embauche:"), 0, 6);
        form.add(dateEmbauche, 1, 6);
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button creerBtn = new Button("Créer");
        creerBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;"
        );
        creerBtn.setOnAction(e -> {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || 
                emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                messageLabel.setText("Veuillez remplir tous les champs obligatoires");
                messageLabel.setTextFill(Color.web("#e74c3c"));
                return;
            }
            
            Utilisateur nouveauUser = new Utilisateur(
                0, nomField.getText(), prenomField.getText(), emailField.getText(),
                passwordField.getText(), roleCombo.getValue(), deptField.getText(),
                dateEmbauche.getValue(), null
            );
            
            Utilisateur userCree = utilisateurDAO.ajouter(nouveauUser);
            
            // Initialiser les soldes pour le nouvel employé
            if (userCree.getRole() == Role.EMPLOYE) {
                soldeCongeDAO.initialiserSoldesUtilisateur(userCree.getId());
            }
            
            dialog.close();
            afficherUtilisateurs(); // Rafraîchir la liste
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Utilisateur créé avec succès");
            alert.showAndWait();
        });
        
        Button annulerBtn = new Button("Annuler");
        annulerBtn.setOnAction(e -> dialog.close());
        
        buttonsBox.getChildren().addAll(creerBtn, annulerBtn);
        
        content.getChildren().addAll(titleLabel, form, messageLabel, buttonsBox);
        
        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void afficherTypesConges() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Types de congés");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button ajouterBtn = new Button("➕ Nouveau type");
        ajouterBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;"
        );
        ajouterBtn.setOnAction(e -> afficherDialogueAjoutTypeConge());
        
        titleBox.getChildren().addAll(titleLabel, spacer, ajouterBtn);
        
        // Liste des types de congés
        VBox typesBox = new VBox(10);
        List<TypeConge> types = typeCongeDAO.getTous();
        
        for (TypeConge type : types) {
            HBox typeCard = new HBox(20);
            typeCard.setAlignment(Pos.CENTER_LEFT);
            typeCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 20;" +
                "-fx-background-radius: 5;"
            );
            
            VBox infoBox = new VBox(5);
            Label nomLabel = new Label(type.getNom());
            nomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            Label descLabel = new Label(type.getDescription());
            descLabel.setTextFill(Color.web("#7f8c8d"));
            
            infoBox.getChildren().addAll(nomLabel, descLabel);
            
            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            
            Label joursLabel = new Label(type.getJoursMaxParAn() + " jours/an");
            joursLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
            joursLabel.setTextFill(Color.web("#3498db"));
            
            Label payeLabel = new Label(type.isEstPayé() ? "Payé" : "Non payé");
            payeLabel.setTextFill(type.isEstPayé() ? Color.web("#27ae60") : Color.web("#e67e22"));
            
            typeCard.getChildren().addAll(infoBox, spacer2, joursLabel, payeLabel);
            typesBox.getChildren().add(typeCard);
        }
        
        content.getChildren().addAll(titleBox, typesBox);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherSoldes() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Ajustement des soldes annuels");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(500);
        formBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 30;" +
            "-fx-background-radius: 10;"
        );
        
        Label utilisateurLabel = new Label("Sélectionner un utilisateur:");
        ComboBox<Utilisateur> userCombo = new ComboBox<>();
        List<Utilisateur> employes = utilisateurDAO.getParRole(Role.EMPLOYE);
        userCombo.setItems(FXCollections.observableArrayList(employes));
        userCombo.setPrefWidth(Double.MAX_VALUE);
        
        Label typeLabel = new Label("Type de congé:");
        ComboBox<TypeConge> typeCombo = new ComboBox<>();
        typeCombo.setItems(FXCollections.observableArrayList(typeCongeDAO.getTous()));
        typeCombo.setPrefWidth(Double.MAX_VALUE);
        
        Label joursLabel = new Label("Nouveaux jours alloués:");
        TextField joursField = new TextField();
        joursField.setPromptText("Nombre de jours");
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        Button ajusterBtn = new Button("Ajuster le solde");
        ajusterBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );
        ajusterBtn.setOnAction(e -> {
            if (userCombo.getValue() == null || typeCombo.getValue() == null || 
                joursField.getText().isEmpty()) {
                messageLabel.setText("Veuillez remplir tous les champs");
                messageLabel.setTextFill(Color.web("#e74c3c"));
                return;
            }
            
            try {
                double nouveauxJours = Double.parseDouble(joursField.getText());
                int annee = LocalDate.now().getYear();
                
                var soldeOpt = soldeCongeDAO.getParUtilisateurEtType(
                    userCombo.getValue().getId(),
                    typeCombo.getValue().getId(),
                    annee
                );
                
                if (soldeOpt.isPresent()) {
                    SoldeConge solde = soldeOpt.get();
                    solde.setJoursAlloues(nouveauxJours);
                    soldeCongeDAO.modifier(solde);
                    
                    messageLabel.setText("Solde ajusté avec succès!");
                    messageLabel.setTextFill(Color.web("#27ae60"));
                } else {
                    messageLabel.setText("Aucun solde trouvé pour cet utilisateur et ce type");
                    messageLabel.setTextFill(Color.web("#e74c3c"));
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Veuillez entrer un nombre valide");
                messageLabel.setTextFill(Color.web("#e74c3c"));
            }
        });
        
        formBox.getChildren().addAll(
            utilisateurLabel, userCombo,
            typeLabel, typeCombo,
            joursLabel, joursField,
            ajusterBtn, messageLabel
        );
        
        content.getChildren().addAll(titleLabel, formBox);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherDialogueAjoutTypeConge() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Ajouter un type de congé");
        
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(30));
        dialogVbox.setStyle("-fx-background-color: white;");
        
        // Formulaire
        VBox formBox = new VBox(15);
        
        Label nomLabel = new Label("Nom du type de congé:");
        TextField nomField = new TextField();
        nomField.setPromptText("Ex: Congé exceptionnel");
        nomField.setPrefWidth(400);
        
        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description du type de congé...");
        descArea.setPrefRowCount(3);
        descArea.setPrefWidth(400);
        
        Label joursLabel = new Label("Jours maximum par an:");
        TextField joursField = new TextField();
        joursField.setPromptText("Ex: 10");
        joursField.setPrefWidth(400);
        
        Label payeLabel = new Label("Type de congé:");
        CheckBox payeCheckBox = new CheckBox("Congé payé");
        payeCheckBox.setSelected(true);
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        formBox.getChildren().addAll(
            nomLabel, nomField,
            descLabel, descArea,
            joursLabel, joursField,
            payeLabel, payeCheckBox,
            messageLabel
        );
        
        // Boutons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle(
            "-fx-background-color: #95a5a6;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 20;"
        );
        cancelBtn.setOnAction(e -> dialog.close());
        
        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle(
            "-fx-background-color: #27ae60;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 20;" +
            "-fx-font-weight: bold;"
        );
        saveBtn.setOnAction(e -> {
            try {
                // Validation
                if (nomField.getText().trim().isEmpty()) {
                    messageLabel.setText("Le nom est obligatoire");
                    messageLabel.setTextFill(Color.web("#e74c3c"));
                    return;
                }
                
                if (joursField.getText().trim().isEmpty()) {
                    messageLabel.setText("Le nombre de jours est obligatoire");
                    messageLabel.setTextFill(Color.web("#e74c3c"));
                    return;
                }
                
                int joursMax;
                try {
                    joursMax = Integer.parseInt(joursField.getText().trim());
                    if (joursMax <= 0) {
                        messageLabel.setText("Le nombre de jours doit être positif");
                        messageLabel.setTextFill(Color.web("#e74c3c"));
                        return;
                    }
                } catch (NumberFormatException ex) {
                    messageLabel.setText("Le nombre de jours doit être un nombre entier");
                    messageLabel.setTextFill(Color.web("#e74c3c"));
                    return;
                }
                
                // Créer le type de congé
                TypeConge nouveauType = new TypeConge();
                nouveauType.setNom(nomField.getText().trim());
                nouveauType.setDescription(descArea.getText().trim());
                nouveauType.setJoursMaxParAn(joursMax);
                nouveauType.setEstPayé(payeCheckBox.isSelected());
                
                // Enregistrer dans la base de données
                typeCongeDAO.ajouter(nouveauType);
                
                // Rafraîchir l'affichage
                afficherTypesConges();
                
                dialog.close();
                
                // Message de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le type de congé a été ajouté avec succès!");
                alert.showAndWait();
                
            } catch (Exception ex) {
                messageLabel.setText("Erreur: " + ex.getMessage());
                messageLabel.setTextFill(Color.web("#e74c3c"));
                ex.printStackTrace();
            }
        });
        
        buttonsBox.getChildren().addAll(cancelBtn, saveBtn);
        
        dialogVbox.getChildren().addAll(
            new Label("Nouveau type de congé"),
            formBox,
            buttonsBox
        );
        
        Scene dialogScene = new Scene(dialogVbox);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
    
    private void deconnecter() {
        SessionManager.getInstance().deconnecter();
        LoginView loginView = new LoginView();
        loginView.show(stage);
    }
}
