package com.conges.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.conges.dao.DemandeCongeDAO;
import com.conges.dao.SoldeCongeDAO;
import com.conges.dao.TypeCongeDAO;
import com.conges.dao.UtilisateurDAO;
import com.conges.model.DemandeConge;
import com.conges.model.SoldeConge;
import com.conges.model.StatutDemande;
import com.conges.model.TypeConge;
import com.conges.model.Utilisateur;

/**
 * Service pour gérer les demandes de congés
 */
public class CongeService {
    private static CongeService instance;
    private DemandeCongeDAO demandeDAO;
    private SoldeCongeDAO soldeDAO;
    private TypeCongeDAO typeCongeDAO;
    private UtilisateurDAO utilisateurDAO;
    
    private CongeService() {
        this.demandeDAO = DemandeCongeDAO.getInstance();
        this.soldeDAO = SoldeCongeDAO.getInstance();
        this.typeCongeDAO = TypeCongeDAO.getInstance();
        this.utilisateurDAO = UtilisateurDAO.getInstance();
    }
    
    public static CongeService getInstance() {
        if (instance == null) {
            instance = new CongeService();
        }
        return instance;
    }
    
    /**
     * Soumet une nouvelle demande de congé
     */
    public DemandeConge soumettreDemande(int idUtilisateur, int idTypeConge, 
                                         LocalDate dateDebut, LocalDate dateFin, String motif) 
            throws Exception {
        
        // Vérifier que la date de début est avant la date de fin
        if (dateDebut.isAfter(dateFin)) {
            throw new Exception("La date de début doit être avant la date de fin");
        }
        
        // Vérifier que les dates ne sont pas dans le passé
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new Exception("La date de début ne peut pas être dans le passé");
        }
        
        // Créer la demande
        DemandeConge demande = new DemandeConge(0, idUtilisateur, idTypeConge, 
            dateDebut, dateFin, motif);
        
        // Vérifier le solde disponible
        int annee = dateDebut.getYear();
        Optional<SoldeConge> soldeOpt = soldeDAO.getParUtilisateurEtType(
            idUtilisateur, idTypeConge, annee);
        
        SoldeConge solde;
        if (soldeOpt.isPresent()) {
            solde = soldeOpt.get();
        } else {
            // Créer automatiquement le solde s'il n'existe pas
            Optional<TypeConge> typeCongeOpt = typeCongeDAO.getParId(idTypeConge);
            if (!typeCongeOpt.isPresent()) {
                throw new Exception("Type de congé introuvable");
            }
            
            TypeConge typeConge = typeCongeOpt.get();
            solde = new SoldeConge(0, idUtilisateur, idTypeConge, 
                annee, typeConge.getJoursMaxParAn(), 0, 0);
            solde = soldeDAO.ajouter(solde);
            
            System.out.println("Solde créé automatiquement pour l'utilisateur #" + idUtilisateur + 
                " - Type: " + typeConge.getNom() + " - Année: " + annee);
        }
        
        // Vérifier que le solde est suffisant
        if (solde.getSoldeDisponible() < demande.getNombreJours()) {
            throw new Exception("Solde insuffisant pour ce type de congé");
        }
        
        // Mettre à jour le solde en attente
        solde.setJoursEnAttente(solde.getJoursEnAttente() + demande.getNombreJours());
        soldeDAO.modifier(solde);
        
        return demandeDAO.ajouter(demande);
    }
    
    /**
     * Approuve une demande de congé
     */
    public void approuverDemande(int idDemande, int idValidateur, String commentaire) 
            throws Exception {
        
        Optional<DemandeConge> demandeOpt = demandeDAO.getParId(idDemande);
        if (!demandeOpt.isPresent()) {
            throw new Exception("Demande introuvable");
        }
        
        DemandeConge demande = demandeOpt.get();
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new Exception("Seules les demandes en attente peuvent être approuvées");
        }
        
        // Mettre à jour la demande
        demande.setStatut(StatutDemande.APPROUVEE);
        demande.setIdValidateur(idValidateur);
        demande.setDateValidation(LocalDate.now());
        demande.setCommentaireValidation(commentaire);
        demandeDAO.modifier(demande);
        
        // Mettre à jour le solde
        int annee = demande.getDateDebut().getYear();
        Optional<SoldeConge> soldeOpt = soldeDAO.getParUtilisateurEtType(
            demande.getIdUtilisateur(), demande.getIdTypeConge(), annee);
        
        if (soldeOpt.isPresent()) {
            SoldeConge solde = soldeOpt.get();
            solde.setJoursEnAttente(solde.getJoursEnAttente() - demande.getNombreJours());
            solde.setJoursUtilises(solde.getJoursUtilises() + demande.getNombreJours());
            soldeDAO.modifier(solde);
        }
    }
    
    /**
     * Refuse une demande de congé
     */
    public void refuserDemande(int idDemande, int idValidateur, String commentaire) 
            throws Exception {
        
        Optional<DemandeConge> demandeOpt = demandeDAO.getParId(idDemande);
        if (!demandeOpt.isPresent()) {
            throw new Exception("Demande introuvable");
        }
        
        DemandeConge demande = demandeOpt.get();
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new Exception("Seules les demandes en attente peuvent être refusées");
        }
        
        // Mettre à jour la demande
        demande.setStatut(StatutDemande.REFUSEE);
        demande.setIdValidateur(idValidateur);
        demande.setDateValidation(LocalDate.now());
        demande.setCommentaireValidation(commentaire);
        demandeDAO.modifier(demande);
        
        // Libérer le solde en attente
        int annee = demande.getDateDebut().getYear();
        Optional<SoldeConge> soldeOpt = soldeDAO.getParUtilisateurEtType(
            demande.getIdUtilisateur(), demande.getIdTypeConge(), annee);
        
        if (soldeOpt.isPresent()) {
            SoldeConge solde = soldeOpt.get();
            solde.setJoursEnAttente(solde.getJoursEnAttente() - demande.getNombreJours());
            soldeDAO.modifier(solde);
        }
    }
    
    /**
     * Récupère les demandes d'un employé
     */
    public List<DemandeConge> getDemandesEmploye(int idEmploye) {
        return demandeDAO.getParUtilisateur(idEmploye);
    }
    
    /**
     * Récupère les demandes de l'équipe d'un manager
     */
    public List<DemandeConge> getDemandesEquipe(int idManager) {
        List<Utilisateur> employes = utilisateurDAO.getEmployesParManager(idManager);
        List<Integer> idsEmployes = employes.stream()
            .map(Utilisateur::getId)
            .toList();
        return demandeDAO.getDemandesEquipe(idsEmployes);
    }
    
    /**
     * Récupère le solde d'un employé pour l'année en cours
     */
    public List<SoldeConge> getSoldesEmploye(int idEmploye) {
        int anneeActuelle = LocalDate.now().getYear();
        return soldeDAO.getParUtilisateur(idEmploye, anneeActuelle);
    }
    
    /**
     * Récupère tous les types de congés
     */
    public List<TypeConge> getTypesConge() {
        return typeCongeDAO.getTous();
    }
}
