package com.conges.model;

/**
 * Classe représentant un type de congé
 */
public class TypeConge {
    private int id;
    private String nom;
    private String description;
    private int joursMaxParAn;
    private boolean estPayé;
    
    public TypeConge() {
    }
    
    public TypeConge(int id, String nom, String description, int joursMaxParAn, boolean estPayé) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.joursMaxParAn = joursMaxParAn;
        this.estPayé = estPayé;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getJoursMaxParAn() {
        return joursMaxParAn;
    }
    
    public void setJoursMaxParAn(int joursMaxParAn) {
        this.joursMaxParAn = joursMaxParAn;
    }
    
    public boolean isEstPayé() {
        return estPayé;
    }
    
    public void setEstPayé(boolean estPayé) {
        this.estPayé = estPayé;
    }
    
    @Override
    public String toString() {
        return nom;
    }
}
