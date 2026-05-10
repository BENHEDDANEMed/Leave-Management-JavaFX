package com.conges.model;

/**
 * Énumération des statuts possibles pour une demande de congé
 */
public enum StatutDemande {
    EN_ATTENTE("En attente"),
    APPROUVEE("Approuvée"),
    REFUSEE("Refusée"),
    ANNULEE("Annulée");
    
    private final String libelle;
    
    StatutDemande(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
