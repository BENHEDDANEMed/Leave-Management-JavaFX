package com.conges.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.conges.database.DatabaseConnection;
import com.conges.model.DemandeConge;
import com.conges.model.StatutDemande;

/**
 * DAO pour gérer les demandes de congés (MySQL)
 */
public class DemandeCongeDAO {
    private static DemandeCongeDAO instance;
    private Connection connection;
    
    private DemandeCongeDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public static DemandeCongeDAO getInstance() {
        if (instance == null) {
            instance = new DemandeCongeDAO();
        }
        return instance;
    }
    
    public List<DemandeConge> getTous() {
        List<DemandeConge> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_conge ORDER BY date_creation DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeConge(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes");
            e.printStackTrace();
        }
        
        return demandes;
    }
    
    public Optional<DemandeConge> getParId(int id) {
        String sql = "SELECT * FROM demandes_conge WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDemandeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la demande #" + id);
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<DemandeConge> getParUtilisateur(int idUtilisateur) {
        List<DemandeConge> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_conge WHERE id_utilisateur = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    demandes.add(mapResultSetToDemandeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes de l'utilisateur #" + idUtilisateur);
            e.printStackTrace();
        }
        
        return demandes;
    }
    
    public List<DemandeConge> getParStatut(StatutDemande statut) {
        List<DemandeConge> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes_conge WHERE statut = ? ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    demandes.add(mapResultSetToDemandeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes par statut");
            e.printStackTrace();
        }
        
        return demandes;
    }
    
    public List<DemandeConge> getDemandesEquipe(List<Integer> idsEmployes) {
        if (idsEmployes == null || idsEmployes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<DemandeConge> demandes = new ArrayList<>();
        String placeholders = String.join(",", idsEmployes.stream().map(id -> "?").toArray(String[]::new));
        String sql = "SELECT * FROM demandes_conge WHERE id_utilisateur IN (" + placeholders + ") ORDER BY date_creation DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < idsEmployes.size(); i++) {
                pstmt.setInt(i + 1, idsEmployes.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    demandes.add(mapResultSetToDemandeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes de l'équipe");
            e.printStackTrace();
        }
        
        return demandes;
    }
    
    public DemandeConge ajouter(DemandeConge demande) {
        String sql = "INSERT INTO demandes_conge (id_utilisateur, id_type_conge, date_debut, date_fin, " +
                    "nombre_jours, motif, statut, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("Tentative d'ajout de demande pour l'utilisateur " + demande.getIdUtilisateur()); // Debug
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, demande.getIdUtilisateur());
            pstmt.setInt(2, demande.getIdTypeConge());
            pstmt.setDate(3, Date.valueOf(demande.getDateDebut()));
            pstmt.setDate(4, Date.valueOf(demande.getDateFin()));
            pstmt.setDouble(5, demande.getNombreJours());
            pstmt.setString(6, demande.getMotif());
            pstmt.setString(7, demande.getStatut().name());
            pstmt.setTimestamp(8, Timestamp.valueOf(demande.getDateCreation().atStartOfDay()));
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Lignes affectées: " + affectedRows); // Debug
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        demande.setId(generatedKeys.getInt(1));
                        System.out.println("Demande ajoutée avec l'ID: " + demande.getId()); // Debug
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la demande");
            e.printStackTrace();
        }
        
        return demande;
    }
    
    public boolean modifier(DemandeConge demande) {
        String sql = "UPDATE demandes_conge SET id_utilisateur = ?, id_type_conge = ?, date_debut = ?, " +
                    "date_fin = ?, motif = ?, statut = ?, id_validateur = ?, date_validation = ?, " +
                    "commentaire_validation = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, demande.getIdUtilisateur());
            pstmt.setInt(2, demande.getIdTypeConge());
            pstmt.setDate(3, Date.valueOf(demande.getDateDebut()));
            pstmt.setDate(4, Date.valueOf(demande.getDateFin()));
            pstmt.setString(5, demande.getMotif());
            pstmt.setString(6, demande.getStatut().name());
            
            if (demande.getIdValidateur() != null) {
                pstmt.setInt(7, demande.getIdValidateur());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            if (demande.getDateValidation() != null) {
                pstmt.setDate(8, Date.valueOf(demande.getDateValidation()));
            } else {
                pstmt.setNull(8, Types.DATE);
            }
            
            pstmt.setString(9, demande.getCommentaireValidation());
            pstmt.setInt(10, demande.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la demande #" + demande.getId());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimer(int id) {
        String sql = "DELETE FROM demandes_conge WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la demande #" + id);
            e.printStackTrace();
            return false;
        }
    }
    
    private DemandeConge mapResultSetToDemandeConge(ResultSet rs) throws SQLException {
        DemandeConge demande = new DemandeConge();
        demande.setId(rs.getInt("id"));
        demande.setIdUtilisateur(rs.getInt("id_utilisateur"));
        demande.setIdTypeConge(rs.getInt("id_type_conge"));
        
        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            demande.setDateDebut(dateDebut.toLocalDate());
        }
        
        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            demande.setDateFin(dateFin.toLocalDate());
        }
        
        demande.setMotif(rs.getString("motif"));
        demande.setStatut(StatutDemande.valueOf(rs.getString("statut")));
        
        int idValidateur = rs.getInt("id_validateur");
        if (!rs.wasNull()) {
            demande.setIdValidateur(idValidateur);
        }
        
        Date dateValidation = rs.getDate("date_validation");
        if (dateValidation != null) {
            demande.setDateValidation(dateValidation.toLocalDate());
        }
        
        demande.setCommentaireValidation(rs.getString("commentaire_validation"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            demande.setDateCreation(dateCreation.toLocalDateTime().toLocalDate());
        }
        
        return demande;
    }
}
