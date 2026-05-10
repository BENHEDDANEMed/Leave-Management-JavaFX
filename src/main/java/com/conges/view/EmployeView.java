package com.conges.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.conges.model.DemandeConge;
import com.conges.model.SoldeConge;
import com.conges.model.StatutDemande;
import com.conges.model.TypeConge;
import com.conges.model.Utilisateur;
import com.conges.service.CongeService;
import com.conges.service.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Vue du tableau de bord employé
 */
public class EmployeView {
    private Stage stage;
    private Utilisateur utilisateur;
    private CongeService congeService;
    private TableView<DemandeConge> demandesTable;
    private VBox soldesBox;
    
    public EmployeView() {
        this.utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        this.congeService = CongeService.getInstance();
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        stage.setTitle("Tableau de bord - Employé");
        stage.setWidth(1000);
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
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);
        
        Label titleLabel = new Label("Système de Gestion des Congés");
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
        menu.setPrefWidth(200);
        menu.setStyle("-fx-background-color: #34495e; -fx-padding: 20;");
        
        Button btnAccueil = creerBoutonMenu("🏠 Accueil");
        btnAccueil.setOnAction(e -> afficherAccueil());
        
        Button btnSoldes = creerBoutonMenu("💰 Mes soldes");
        btnSoldes.setOnAction(e -> afficherSoldes());
        
        Button btnNouvelleDemande = creerBoutonMenu("➕ Nouvelle demande");
        btnNouvelleDemande.setOnAction(e -> afficherNouvelleDemande());
        
        Button btnMesDemandes = creerBoutonMenu("📋 Mes demandes");
        btnMesDemandes.setOnAction(e -> afficherMesDemandes());
        
        menu.getChildren().addAll(btnAccueil, btnSoldes, btnNouvelleDemande, btnMesDemandes);
        
        return menu;
    }
    
    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setPrefWidth(160);
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
                "-fx-background-color: #2c3e50;" +
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
        
        Label welcomeLabel = new Label("Bienvenue, " + utilisateur.getPrenom() + " !");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.web("#2c3e50"));
        
        // Cartes de statistiques
        HBox statsBox = new HBox(20);
        
        List<SoldeConge> soldes = congeService.getSoldesEmploye(utilisateur.getId());
        List<DemandeConge> demandes = congeService.getDemandesEmploye(utilisateur.getId());
        
        long demandesEnAttente = demandes.stream()
            .filter(d -> d.getStatut() == StatutDemande.EN_ATTENTE)
            .count();
        
        VBox card1 = creerCarte("Demandes en attente", String.valueOf(demandesEnAttente), "#3498db");
        VBox card2 = creerCarte("Total demandes", String.valueOf(demandes.size()), "#2ecc71");
        VBox card3 = creerCarte("Types de congés", String.valueOf(soldes.size()), "#e67e22");
        
        statsBox.getChildren().addAll(card1, card2, card3);
        
        // Soldes rapides
        soldesBox = new VBox(10);
        Label soldesTitle = new Label("Vos soldes de congés");
        soldesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        soldesBox.getChildren().add(soldesTitle);
        
        afficherSoldesResume();
        
        content.getChildren().addAll(welcomeLabel, statsBox, soldesBox);
        
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
    
    private void afficherSoldesResume() {
        List<SoldeConge> soldes = congeService.getSoldesEmploye(utilisateur.getId());
        Map<Integer, TypeConge> typesMap = congeService.getTypesConge().stream()
            .collect(Collectors.toMap(TypeConge::getId, t -> t));
        
        for (SoldeConge solde : soldes) {
            TypeConge type = typesMap.get(solde.getIdTypeConge());
            if (type != null) {
                HBox soldeBox = new HBox(15);
                soldeBox.setAlignment(Pos.CENTER_LEFT);
                soldeBox.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-padding: 15;" +
                    "-fx-background-radius: 5;"
                );
                
                Label typeLabel = new Label(type.getNom());
                typeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
                typeLabel.setPrefWidth(150);
                
                Label disponibleLabel = new Label(
                    String.format("Disponible: %.1f jours", solde.getSoldeDisponible())
                );
                disponibleLabel.setFont(Font.font("Arial", 12));
                disponibleLabel.setTextFill(Color.web("#27ae60"));
                
                Label utilisesLabel = new Label(
                    String.format("Utilisés: %.1f", solde.getJoursUtilises())
                );
                utilisesLabel.setFont(Font.font("Arial", 12));
                utilisesLabel.setTextFill(Color.web("#95a5a6"));
                
                soldeBox.getChildren().addAll(typeLabel, disponibleLabel, utilisesLabel);
                soldesBox.getChildren().add(soldeBox);
            }
        }
    }
    
    private void afficherAccueil() {
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(creerContenuAccueil());
    }
    
    private void afficherSoldes() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Mes Soldes de Congés");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        soldesBox = new VBox(15);
        afficherSoldesResume();
        
        content.getChildren().addAll(titleLabel, soldesBox);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherNouvelleDemande() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setMaxWidth(600);
        
        Label titleLabel = new Label("Nouvelle Demande de Congé");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        VBox formBox = new VBox(15);
        formBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 30;" +
            "-fx-background-radius: 10;"
        );
        
        // Type de congé
        Label typeLabel = new Label("Type de congé:");
        ComboBox<TypeConge> typeCombo = new ComboBox<>();
        List<TypeConge> typesConge = congeService.getTypesConge();
        System.out.println("Nombre de types de congé chargés: " + typesConge.size()); // Debug
        typesConge.forEach(type -> System.out.println("- " + type.getNom())); // Debug
        typeCombo.setItems(FXCollections.observableArrayList(typesConge));
        typeCombo.setPrefWidth(Double.MAX_VALUE);
        
        // Date de début
        Label dateDebutLabel = new Label("Date de début:");
        DatePicker dateDebutPicker = new DatePicker();
        dateDebutPicker.setPrefWidth(Double.MAX_VALUE);
        
        // Date de fin
        Label dateFinLabel = new Label("Date de fin:");
        DatePicker dateFinPicker = new DatePicker();
        dateFinPicker.setPrefWidth(Double.MAX_VALUE);
        
        // Motif
        Label motifLabel = new Label("Motif:");
        TextArea motifArea = new TextArea();
        motifArea.setPrefRowCount(3);
        motifArea.setPromptText("Décrivez la raison de votre demande...");
        
        // Message
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        // Bouton soumettre
        Button submitBtn = new Button("Soumettre la demande");
        submitBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-pref-width: 200;"
        );
        submitBtn.setOnAction(e -> {
            if (typeCombo.getValue() == null || dateDebutPicker.getValue() == null || 
                dateFinPicker.getValue() == null || motifArea.getText().trim().isEmpty()) {
                messageLabel.setText("Veuillez remplir tous les champs");
                messageLabel.setTextFill(Color.web("#e74c3c"));
                return;
            }
            
            try {
                congeService.soumettreDemande(
                    utilisateur.getId(),
                    typeCombo.getValue().getId(),
                    dateDebutPicker.getValue(),
                    dateFinPicker.getValue(),
                    motifArea.getText()
                );
                messageLabel.setText("Demande soumise avec succès!");
                messageLabel.setTextFill(Color.web("#27ae60"));
                
                // Réinitialiser le formulaire
                typeCombo.setValue(null);
                dateDebutPicker.setValue(null);
                dateFinPicker.setValue(null);
                motifArea.clear();
                
                // Actualiser les soldes
                soldesBox.getChildren().clear();
                afficherSoldesResume();
                
            } catch (Exception ex) {
                messageLabel.setText("Erreur: " + ex.getMessage());
                messageLabel.setTextFill(Color.web("#e74c3c"));
                ex.printStackTrace(); // Afficher l'erreur dans la console
            }
        });
        
        formBox.getChildren().addAll(
            typeLabel, typeCombo,
            dateDebutLabel, dateDebutPicker,
            dateFinLabel, dateFinPicker,
            motifLabel, motifArea,
            submitBtn, messageLabel
        );
        
        content.getChildren().addAll(titleLabel, formBox);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherMesDemandes() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Mes Demandes de Congés");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Tableau des demandes
        demandesTable = new TableView<>();
        demandesTable.setStyle("-fx-background-color: white;");
        
        TableColumn<DemandeConge, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            int idType = cellData.getValue().getIdTypeConge();
            String nomType = congeService.getTypesConge().stream()
                .filter(t -> t.getId() == idType)
                .findFirst()
                .map(TypeConge::getNom)
                .orElse("N/A");
            return new SimpleStringProperty(nomType);
        });
        typeCol.setPrefWidth(150);
        
        TableColumn<DemandeConge, LocalDate> dateDebutCol = new TableColumn<>("Date début");
        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateDebutCol.setPrefWidth(120);
        
        TableColumn<DemandeConge, LocalDate> dateFinCol = new TableColumn<>("Date fin");
        dateFinCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        dateFinCol.setPrefWidth(120);
        
        TableColumn<DemandeConge, Double> nbJoursCol = new TableColumn<>("Nb jours");
        nbJoursCol.setCellValueFactory(new PropertyValueFactory<>("nombreJours"));
        nbJoursCol.setPrefWidth(80);
        
        TableColumn<DemandeConge, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().getLibelle())
        );
        statutCol.setPrefWidth(120);
        statutCol.setCellFactory(column -> new TableCell<DemandeConge, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Approuvée")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (item.equals("Refusée")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else if (item.equals("En attente")) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        demandesTable.getColumns().addAll(typeCol, dateDebutCol, dateFinCol, nbJoursCol, statutCol);
        
        // Bouton pour actualiser
        Button refreshBtn = new Button("🔄 Actualiser");
        refreshBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 15;"
        );
        refreshBtn.setOnAction(e -> rafraichirDemandes());
        
        // Charger les données
        rafraichirDemandes();
        
        content.getChildren().addAll(titleLabel, refreshBtn, demandesTable);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void rafraichirDemandes() {
        if (demandesTable != null) {
            List<DemandeConge> demandes = congeService.getDemandesEmploye(utilisateur.getId());
            System.out.println("Nombre de demandes chargées: " + demandes.size()); // Debug
            demandesTable.setItems(FXCollections.observableArrayList(demandes));
            demandesTable.refresh();
        }
    }
    
    private void deconnecter() {
        SessionManager.getInstance().deconnecter();
        LoginView loginView = new LoginView();
        loginView.show(stage);
    }
}
