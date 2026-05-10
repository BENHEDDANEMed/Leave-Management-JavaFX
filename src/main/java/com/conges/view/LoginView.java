package com.conges.view;

import java.util.Optional;

import com.conges.dao.UtilisateurDAO;
import com.conges.model.Utilisateur;
import com.conges.service.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Vue de connexion au système
 */
public class LoginView {
    private Stage stage;
    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;
    
    public void show(Stage primaryStage) {
        this.stage = primaryStage;
        
        // Configuration de la fenêtre
        stage.setTitle("Système de Gestion des Congés - Connexion");
        stage.setWidth(450);
        stage.setHeight(500);
        
        // Création du layout principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Conteneur du formulaire
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(350);
        formBox.setPadding(new Insets(30));
        formBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );
        
        // Titre
        Label titleLabel = new Label("Connexion");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        
        Label subtitleLabel = new Label("Système de Gestion des Congés");
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#7f8c8d"));
        
        // Champ email
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        emailField = new TextField();
        emailField.setPromptText("Entrez votre email");
        emailField.setPrefHeight(35);
        emailField.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-color: #dcdde1;" +
            "-fx-border-radius: 5;"
        );
        
        // Champ mot de passe
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        passwordField.setPrefHeight(35);
        passwordField.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-color: #dcdde1;" +
            "-fx-border-radius: 5;"
        );
        
        // Bouton de connexion
        Button loginButton = new Button("Se connecter");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(40);
        loginButton.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        loginButton.setOnAction(e -> handleLogin());
        
        // Effet hover pour le bouton
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle(
                "-fx-background-color: #2980b9;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Label pour les messages
        messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 11));
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Informations de test
        VBox infoBox = new VBox(5);
        infoBox.setStyle(
            "-fx-background-color: #ecf0f1;" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;"
        );
        Label infoTitle = new Label("Comptes de test:");
        infoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        Label info1 = new Label("Admin: admin@conges.com / admin123");
        Label info2 = new Label("Manager: ayoub@conges.com / ayoub123");
        Label info3 = new Label("Employé: mohamed@conges.com / simo123");
        Label info4 = new Label("Employé: hamza@conges.com / hamza123");

        info1.setFont(Font.font("Arial", 10));
        info2.setFont(Font.font("Arial", 10));
        info3.setFont(Font.font("Arial", 10));
        info4.setFont(Font.font("Arial", 10));
        infoBox.getChildren().addAll(infoTitle, info1, info2, info3, info4);
        
        // Action sur Enter
        passwordField.setOnAction(e -> handleLogin());
        
        // Ajout des composants au formulaire
        formBox.getChildren().addAll(
            titleLabel, subtitleLabel,
            emailLabel, emailField,
            passwordLabel, passwordField,
            loginButton, messageLabel
        );
        
        root.getChildren().addAll(formBox, infoBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // Focus sur le champ email
        emailField.requestFocus();
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Veuillez remplir tous les champs", "error");
            return;
        }
        
        // Authentification
        UtilisateurDAO utilisateurDAO = UtilisateurDAO.getInstance();
        Optional<Utilisateur> userOpt = utilisateurDAO.authentifier(email, password);
        
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            SessionManager.getInstance().setUtilisateurConnecte(user);
            showMessage("Connexion réussie!", "success");
            
            // Rediriger vers le tableau de bord approprié
            ouvrirTableauDeBord(user);
        } else {
            showMessage("Email ou mot de passe incorrect", "error");
            passwordField.clear();
        }
    }
    
    private void ouvrirTableauDeBord(Utilisateur user) {
        switch (user.getRole()) {
            case EMPLOYE:
                EmployeView employeView = new EmployeView();
                employeView.show(stage);
                break;
            case MANAGER:
                ManagerView managerView = new ManagerView();
                managerView.show(stage);
                break;
            case ADMIN:
                AdminView adminView = new AdminView();
                adminView.show(stage);
                break;
        }
    }
    
    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        if (type.equals("error")) {
            messageLabel.setTextFill(Color.web("#e74c3c"));
        } else {
            messageLabel.setTextFill(Color.web("#27ae60"));
        }
    }
}
