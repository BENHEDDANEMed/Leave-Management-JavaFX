package com.conges.model;

/**
 * Énumération des rôles utilisateurs dans le système
 */
public enum Role {
    EMPLOYE("Employé"),
    MANAGER("Manager"),
    ADMIN("Administrateur");
    
    private final String libelle;
    
    Role(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
