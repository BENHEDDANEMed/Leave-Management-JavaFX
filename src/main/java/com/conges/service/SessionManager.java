package com.conges.service;

import com.conges.model.Utilisateur;

/**
 * Classe singleton pour gérer la session utilisateur
 */
public class SessionManager {
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;
    
    private SessionManager() {
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }
    
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }
    
    public void deconnecter() {
        utilisateurConnecte = null;
    }
}
