package com.conges.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.conges.dao.UtilisateurDAO;
import com.conges.model.DemandeConge;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Vue du tableau de bord manager
 */
public class ManagerView {
    private Stage stage;
    private Utilisateur utilisateur;
    private CongeService congeService;
    private UtilisateurDAO utilisateurDAO;
    private TableView<DemandeConge> demandesTable;
    
    public ManagerView() {
        this.utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        this.congeService = CongeService.getInstance();
        this.utilisateurDAO = UtilisateurDAO.getInstance();
    }
    
    public void show(Stage stage) {
        this.stage = stage;
        stage.setTitle("Tableau de bord - Manager");
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
        header.setStyle("-fx-background-color: #16a085; -fx-padding: 20;");
        
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);
        
        Label titleLabel = new Label("Espace Manager");
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
        menu.setStyle("-fx-background-color: #1abc9c; -fx-padding: 20;");
        
        Button btnAccueil = creerBoutonMenu("🏠 Accueil");
        btnAccueil.setOnAction(e -> afficherAccueil());
        
        Button btnDemandesEquipe = creerBoutonMenu("📋 Demandes équipe");
        btnDemandesEquipe.setOnAction(e -> afficherDemandesEquipe());
        
        Button btnPlanning = creerBoutonMenu("📅 Planning équipe");
        btnPlanning.setOnAction(e -> afficherPlanning());
        
        Button btnEquipe = creerBoutonMenu("👥 Mon équipe");
        btnEquipe.setOnAction(e -> afficherEquipe());
        
        menu.getChildren().addAll(btnAccueil, btnDemandesEquipe, btnPlanning, btnEquipe);
        
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
                "-fx-background-color: #16a085;" +
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
        
        System.out.println("Manager ID: " + utilisateur.getId()); // Debug
        List<DemandeConge> demandes = congeService.getDemandesEquipe(utilisateur.getId());
        System.out.println("Nombre de demandes de l'équipe: " + demandes.size()); // Debug
        long demandesEnAttente = demandes.stream()
            .filter(d -> d.getStatut() == StatutDemande.EN_ATTENTE)
            .count();
        
        List<Utilisateur> employes = utilisateurDAO.getEmployesParManager(utilisateur.getId());
        System.out.println("Nombre d'employés dans l'équipe: " + employes.size()); // Debug
        employes.forEach(emp -> System.out.println("  - " + emp.getNomComplet())); // Debug
        
        VBox card1 = creerCarte("Demandes en attente", String.valueOf(demandesEnAttente), "#e67e22");
        VBox card2 = creerCarte("Total demandes", String.valueOf(demandes.size()), "#3498db");
        VBox card3 = creerCarte("Membres d'équipe", String.valueOf(employes.size()), "#2ecc71");
        
        statsBox.getChildren().addAll(card1, card2, card3);
        
        // Demandes récentes en attente
        Label demandesTitle = new Label("Demandes en attente");
        demandesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        VBox demandesBox = new VBox(10);
        List<DemandeConge> demandesEnAttenteList = demandes.stream()
            .filter(d -> d.getStatut() == StatutDemande.EN_ATTENTE)
            .limit(5)
            .toList();
        
        for (DemandeConge demande : demandesEnAttenteList) {
            demandesBox.getChildren().add(creerCarteDemande(demande));
        }
        
        if (demandesEnAttenteList.isEmpty()) {
            Label aucuneLabel = new Label("Aucune demande en attente");
            aucuneLabel.setTextFill(Color.web("#95a5a6"));
            demandesBox.getChildren().add(aucuneLabel);
        }
        
        content.getChildren().addAll(welcomeLabel, statsBox, demandesTitle, demandesBox);
        
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
    
    private HBox creerCarteDemande(DemandeConge demande) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 5;"
        );
        
        Optional<Utilisateur> employeOpt = utilisateurDAO.getParId(demande.getIdUtilisateur());
        String nomEmploye = employeOpt.map(Utilisateur::getNomComplet).orElse("N/A");
        
        Optional<TypeConge> typeOpt = congeService.getTypesConge().stream()
            .filter(t -> t.getId() == demande.getIdTypeConge())
            .findFirst();
        String nomType = typeOpt.map(TypeConge::getNom).orElse("N/A");
        
        Label employeLabel = new Label(nomEmploye);
        employeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        employeLabel.setPrefWidth(150);
        
        Label typeLabel = new Label(nomType);
        typeLabel.setPrefWidth(120);
        
        Label periodeLabel = new Label(
            demande.getDateDebut() + " → " + demande.getDateFin()
        );
        periodeLabel.setPrefWidth(200);
        
        Label joursLabel = new Label(String.format("%.0f jours", demande.getNombreJours()));
        joursLabel.setPrefWidth(80);
        
        Button validerBtn = new Button("✓");
        validerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        validerBtn.setOnAction(e -> afficherDialogueValidation(demande, true));
        
        Button refuserBtn = new Button("✗");
        refuserBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        refuserBtn.setOnAction(e -> afficherDialogueValidation(demande, false));
        
        card.getChildren().addAll(employeLabel, typeLabel, periodeLabel, joursLabel, 
                                   validerBtn, refuserBtn);
        
        return card;
    }
    
    private void afficherDialogueValidation(DemandeConge demande, boolean approuver) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(approuver ? "Approuver la demande" : "Refuser la demande");
        dialog.setWidth(400);
        dialog.setHeight(250);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label questionLabel = new Label(
            approuver ? "Voulez-vous approuver cette demande ?" 
                      : "Voulez-vous refuser cette demande ?"
        );
        questionLabel.setWrapText(true);
        
        Label commentaireLabel = new Label("Commentaire (optionnel):");
        TextArea commentaireArea = new TextArea();
        commentaireArea.setPrefRowCount(3);
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button confirmerBtn = new Button("Confirmer");
        confirmerBtn.setStyle(
            "-fx-background-color: " + (approuver ? "#27ae60" : "#e74c3c") + ";" +
            "-fx-text-fill: white;"
        );
        confirmerBtn.setOnAction(e -> {
            try {
                if (approuver) {
                    congeService.approuverDemande(demande.getId(), utilisateur.getId(), 
                                                  commentaireArea.getText());
                } else {
                    congeService.refuserDemande(demande.getId(), utilisateur.getId(), 
                                                commentaireArea.getText());
                }
                dialog.close();
                afficherAccueil(); // Rafraîchir l'affichage
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText(
                    "Demande " + (approuver ? "approuvée" : "refusée") + " avec succès"
                );
                alert.showAndWait();
            } catch (Exception ex) {
                messageLabel.setText("Erreur: " + ex.getMessage());
                messageLabel.setTextFill(Color.web("#e74c3c"));
            }
        });
        
        Button annulerBtn = new Button("Annuler");
        annulerBtn.setOnAction(e -> dialog.close());
        
        buttonsBox.getChildren().addAll(confirmerBtn, annulerBtn);
        
        content.getChildren().addAll(questionLabel, commentaireLabel, commentaireArea, 
                                      messageLabel, buttonsBox);
        
        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void afficherAccueil() {
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(creerContenuAccueil());
    }
    
    private void afficherDemandesEquipe() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Demandes de l'équipe");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Tableau des demandes
        demandesTable = new TableView<>();
        demandesTable.setStyle("-fx-background-color: white;");
        
        TableColumn<DemandeConge, String> employeCol = new TableColumn<>("Employé");
        employeCol.setCellValueFactory(cellData -> {
            Optional<Utilisateur> emp = utilisateurDAO.getParId(cellData.getValue().getIdUtilisateur());
            return new SimpleStringProperty(emp.map(Utilisateur::getNomComplet).orElse("N/A"));
        });
        employeCol.setPrefWidth(150);
        
        TableColumn<DemandeConge, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Optional<TypeConge> type = congeService.getTypesConge().stream()
                .filter(t -> t.getId() == cellData.getValue().getIdTypeConge())
                .findFirst();
            return new SimpleStringProperty(type.map(TypeConge::getNom).orElse("N/A"));
        });
        typeCol.setPrefWidth(130);
        
        TableColumn<DemandeConge, LocalDate> dateDebutCol = new TableColumn<>("Date début");
        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateDebutCol.setPrefWidth(110);
        
        TableColumn<DemandeConge, LocalDate> dateFinCol = new TableColumn<>("Date fin");
        dateFinCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        dateFinCol.setPrefWidth(110);
        
        TableColumn<DemandeConge, Double> nbJoursCol = new TableColumn<>("Nb jours");
        nbJoursCol.setCellValueFactory(new PropertyValueFactory<>("nombreJours"));
        nbJoursCol.setPrefWidth(80);
        
        TableColumn<DemandeConge, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().getLibelle())
        );
        statutCol.setPrefWidth(120);
        
        TableColumn<DemandeConge, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approuverBtn = new Button("Approuver");
            private final Button refuserBtn = new Button("Refuser");
            private final HBox pane = new HBox(5, approuverBtn, refuserBtn);
            
            {
                approuverBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                refuserBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                
                approuverBtn.setOnAction(e -> {
                    DemandeConge demande = getTableView().getItems().get(getIndex());
                    afficherDialogueValidation(demande, true);
                });
                
                refuserBtn.setOnAction(e -> {
                    DemandeConge demande = getTableView().getItems().get(getIndex());
                    afficherDialogueValidation(demande, false);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DemandeConge demande = getTableView().getItems().get(getIndex());
                    if (demande.getStatut() == StatutDemande.EN_ATTENTE) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        demandesTable.getColumns().addAll(employeCol, typeCol, dateDebutCol, dateFinCol, 
                                          nbJoursCol, statutCol, actionsCol);
        
        // Charger les données
        List<DemandeConge> demandes = congeService.getDemandesEquipe(utilisateur.getId());
        demandesTable.setItems(FXCollections.observableArrayList(demandes));
        
        content.getChildren().addAll(titleLabel, demandesTable);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherPlanning() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Planning de l'équipe");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        Label infoLabel = new Label("Fonctionnalité à venir : Affichage calendrier des congés de l'équipe");
        infoLabel.setTextFill(Color.web("#95a5a6"));
        
        content.getChildren().addAll(titleLabel, infoLabel);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void afficherEquipe() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Mon équipe");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        List<Utilisateur> employes = utilisateurDAO.getEmployesParManager(utilisateur.getId());
        
        VBox employesBox = new VBox(10);
        for (Utilisateur employe : employes) {
            HBox employeCard = new HBox(20);
            employeCard.setAlignment(Pos.CENTER_LEFT);
            employeCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 15;" +
                "-fx-background-radius: 5;"
            );
            
            Label nomLabel = new Label(employe.getNomComplet());
            nomLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
            nomLabel.setPrefWidth(200);
            
            Label emailLabel = new Label(employe.getEmail());
            emailLabel.setPrefWidth(250);
            
            Label deptLabel = new Label(employe.getDepartement());
            deptLabel.setTextFill(Color.web("#7f8c8d"));
            
            employeCard.getChildren().addAll(nomLabel, emailLabel, deptLabel);
            employesBox.getChildren().add(employeCard);
        }
        
        content.getChildren().addAll(titleLabel, employesBox);
        
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(content);
    }
    
    private void deconnecter() {
        SessionManager.getInstance().deconnecter();
        LoginView loginView = new LoginView();
        loginView.show(stage);
    }
}
