package com.conges.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe représentant une demande de congé
 */
public class DemandeConge {
    private int id;
    private int idUtilisateur;
    private int idTypeConge;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double nombreJours;
    private String motif;
    private StatutDemande statut;
    private LocalDate dateCreation;
    private Integer idValidateur;
    private LocalDate dateValidation;
    private String commentaireValidation;
    
    public DemandeConge() {
        this.dateCreation = LocalDate.now();
        this.statut = StatutDemande.EN_ATTENTE;
    }
    
    public DemandeConge(int id, int idUtilisateur, int idTypeConge, LocalDate dateDebut, 
                       LocalDate dateFin, String motif) {
        this();
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idTypeConge = idTypeConge;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
        this.nombreJours = calculerNombreJours();
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
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
        this.nombreJours = calculerNombreJours();
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
        this.nombreJours = calculerNombreJours();
    }
    
    public double getNombreJours() {
        return nombreJours;
    }
    
    public void setNombreJours(double nombreJours) {
        this.nombreJours = nombreJours;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public StatutDemande getStatut() {
        return statut;
    }
    
    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Integer getIdValidateur() {
        return idValidateur;
    }
    
    public void setIdValidateur(Integer idValidateur) {
        this.idValidateur = idValidateur;
    }
    
    public LocalDate getDateValidation() {
        return dateValidation;
    }
    
    public void setDateValidation(LocalDate dateValidation) {
        this.dateValidation = dateValidation;
    }
    
    public String getCommentaireValidation() {
        return commentaireValidation;
    }
    
    public void setCommentaireValidation(String commentaireValidation) {
        this.commentaireValidation = commentaireValidation;
    }
    
    /**
     * Calcule le nombre de jours ouvrables entre dateDebut et dateFin
     * (Version simplifiée - compte tous les jours)
     */
    private double calculerNombreJours() {
        if (dateDebut != null && dateFin != null) {
            return ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        }
        return 0;
    }
}
