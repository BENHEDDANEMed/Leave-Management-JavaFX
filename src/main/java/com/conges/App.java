package com.conges;

import com.conges.view.LoginView;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application principale du système de gestion des congés
 */
public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Afficher l'écran de connexion
        LoginView loginView = new LoginView();
        loginView.show(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
