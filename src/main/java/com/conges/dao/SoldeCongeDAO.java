package com.conges.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.conges.database.DatabaseConnection;
import com.conges.model.SoldeConge;

/**
 * DAO pour gérer les soldes de congés (MySQL)
 */
public class SoldeCongeDAO {
    private static SoldeCongeDAO instance;
    private Connection connection;
    
    private SoldeCongeDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public static SoldeCongeDAO getInstance() {
        if (instance == null) {
            instance = new SoldeCongeDAO();
        }
        return instance;
    }
    
    public List<SoldeConge> getTous() {
        List<SoldeConge> soldes = new ArrayList<>();
        String sql = "SELECT * FROM soldes_conge ORDER BY annee DESC, id_utilisateur";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                soldes.add(mapResultSetToSoldeConge(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des soldes");
            e.printStackTrace();
        }
        
        return soldes;
    }
    
    public Optional<SoldeConge> getParId(int id) {
        String sql = "SELECT * FROM soldes_conge WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSoldeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du solde #" + id);
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public List<SoldeConge> getParUtilisateur(int idUtilisateur, int annee) {
        List<SoldeConge> soldes = new ArrayList<>();
        String sql = "SELECT * FROM soldes_conge WHERE id_utilisateur = ? AND annee = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            pstmt.setInt(2, annee);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    soldes.add(mapResultSetToSoldeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des soldes de l'utilisateur #" + idUtilisateur);
            e.printStackTrace();
        }
        
        return soldes;
    }
    
    public Optional<SoldeConge> getParUtilisateurEtType(int idUtilisateur, int idTypeConge, int annee) {
        String sql = "SELECT * FROM soldes_conge WHERE id_utilisateur = ? AND id_type_conge = ? AND annee = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUtilisateur);
            pstmt.setInt(2, idTypeConge);
            pstmt.setInt(3, annee);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSoldeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du solde");
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public SoldeConge ajouter(SoldeConge solde) {
        String sql = "INSERT INTO soldes_conge (id_utilisateur, id_type_conge, annee, jours_alloues, jours_utilises, jours_en_attente) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, solde.getIdUtilisateur());
            pstmt.setInt(2, solde.getIdTypeConge());
            pstmt.setInt(3, solde.getAnnee());
            pstmt.setDouble(4, solde.getJoursAlloues());
            pstmt.setDouble(5, solde.getJoursUtilises());
            pstmt.setDouble(6, solde.getJoursEnAttente());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        solde.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du solde");
            e.printStackTrace();
        }
        
        return solde;
    }
    
    public boolean modifier(SoldeConge solde) {
        String sql = "UPDATE soldes_conge SET id_utilisateur = ?, id_type_conge = ?, annee = ?, " +
                    "jours_alloues = ?, jours_utilises = ?, jours_en_attente = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, solde.getIdUtilisateur());
            pstmt.setInt(2, solde.getIdTypeConge());
            pstmt.setInt(3, solde.getAnnee());
            pstmt.setDouble(4, solde.getJoursAlloues());
            pstmt.setDouble(5, solde.getJoursUtilises());
            pstmt.setDouble(6, solde.getJoursEnAttente());
            pstmt.setInt(7, solde.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du solde #" + solde.getId());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimer(int id) {
        String sql = "DELETE FROM soldes_conge WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du solde #" + id);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Initialise les soldes d'un nouvel utilisateur pour l'année en cours
     */
    public void initialiserSoldesUtilisateur(int idUtilisateur) {
        int anneeActuelle = LocalDate.now().getYear();
        TypeCongeDAO typeCongeDAO = TypeCongeDAO.getInstance();
        
        typeCongeDAO.getTous().forEach(typeConge -> {
            SoldeConge solde = new SoldeConge(0, idUtilisateur, typeConge.getId(), 
                anneeActuelle, typeConge.getJoursMaxParAn(), 0, 0);
            ajouter(solde);
        });
    }
    
    private SoldeConge mapResultSetToSoldeConge(ResultSet rs) throws SQLException {
        SoldeConge solde = new SoldeConge();
        solde.setId(rs.getInt("id"));
        solde.setIdUtilisateur(rs.getInt("id_utilisateur"));
        solde.setIdTypeConge(rs.getInt("id_type_conge"));
        solde.setAnnee(rs.getInt("annee"));
        solde.setJoursAlloues(rs.getDouble("jours_alloues"));
        solde.setJoursUtilises(rs.getDouble("jours_utilises"));
        solde.setJoursEnAttente(rs.getDouble("jours_en_attente"));
        return solde;
    }
}
