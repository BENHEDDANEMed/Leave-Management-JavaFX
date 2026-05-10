package com.conges.model;

/**
 * Classe représentant le solde de congés d'un employé pour un type de congé
 */
public class SoldeConge {
    private int id;
    private int idUtilisateur;
    private int idTypeConge;
    private int annee;
    private double joursAlloues;
    private double joursUtilises;
    private double joursEnAttente;
    
    public SoldeConge() {
    }
    
    public SoldeConge(int id, int idUtilisateur, int idTypeConge, int annee, 
                     double joursAlloues, double joursUtilises, double joursEnAttente) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idTypeConge = idTypeConge;
        this.annee = annee;
        this.joursAlloues = joursAlloues;
        this.joursUtilises = joursUtilises;
        this.joursEnAttente = joursEnAttente;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public int getIdTypeConge() {
        return idTypeConge;
    }
    
    public void setIdTypeConge(int idTypeConge) {
        this.idTypeConge = idTypeConge;
    }
    
    public int getAnnee() {
        return annee;
    }
    
    public void setAnnee(int annee) {
        this.annee = annee;
    }
    
    public double getJoursAlloues() {
        return joursAlloues;
    }
    
    public void setJoursAlloues(double joursAlloues) {
        this.joursAlloues = joursAlloues;
    }
    
    public double getJoursUtilises() {
        return joursUtilises;
    }
    
    public void setJoursUtilises(double joursUtilises) {
        this.joursUtilises = joursUtilises;
    }
    
    public double getJoursEnAttente() {
        return joursEnAttente;
    }
    
    public void setJoursEnAttente(double joursEnAttente) {
        this.joursEnAttente = joursEnAttente;
    }
    
    /**
     * Calcule le solde disponible
     */
    public double getSoldeDisponible() {
        return joursAlloues - joursUtilises - joursEnAttente;
    }
}
