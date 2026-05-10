package com.conges.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.conges.database.DatabaseConnection;
import com.conges.model.Role;
import com.conges.model.Utilisateur;

/**
 * DAO pour gérer les utilisateurs avec MySQL
 */
public class UtilisateurDAO {
    private static UtilisateurDAO instance;
    private final Connection connection;
    
    private UtilisateurDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public static UtilisateurDAO getInstance() {
        if (instance == null) {
            instance = new UtilisateurDAO();
        }
        return instance;
    }
    
    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> getTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs");
            e.printStackTrace();
        }
        
        return utilisateurs;
    }
    
    /**
     * Récupère un utilisateur par son ID
     */
    public Optional<Utilisateur> getParId(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur #" + id);
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Récupère un utilisateur par son email
     */
    public Optional<Utilisateur> getParEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur " + email);
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Récupère les utilisateurs par rôle
     */
    public List<Utilisateur> getParRole(Role role) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = ? ORDER BY nom, prenom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs par rôle");
            e.printStackTrace();
        }
        
        return utilisateurs;
    }
    
    /**
     * Récupère les employés d'un manager
     */
    public List<Utilisateur> getEmployesParManager(int idManager) {
        List<Utilisateur> employes = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE id_manager = ? ORDER BY nom, prenom";
        
        System.out.println("Recherche des employés pour le manager #" + idManager); // Debug
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idManager);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employes.add(mapResultSetToUtilisateur(rs));
                }
                System.out.println("Nombre d'employés trouvés: " + employes.size()); // Debug
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des employés du manager #" + idManager);
            e.printStackTrace();
        }
        
        return employes;
    }
    
    /**
     * Ajoute un nouvel utilisateur
     */
    public Utilisateur ajouter(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, " +
                    "departement, date_embauche, id_manager, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getMotDePasse());
            pstmt.setString(5, utilisateur.getRole().name());
            pstmt.setString(6, utilisateur.getDepartement());
            pstmt.setDate(7, Date.valueOf(utilisateur.getDateEmbauche()));
            
            if (utilisateur.getIdManager() != null) {
                pstmt.setInt(8, utilisateur.getIdManager());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setBoolean(9, utilisateur.isActif());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utilisateur.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur");
            e.printStackTrace();
        }
        
        return utilisateur;
    }
    
    /**
     * Modifie un utilisateur existant
     */
    public boolean modifier(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, " +
                    "role = ?, departement = ?, date_embauche = ?, id_manager = ?, actif = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getMotDePasse());
            pstmt.setString(5, utilisateur.getRole().name());
            pstmt.setString(6, utilisateur.getDepartement());
            pstmt.setDate(7, Date.valueOf(utilisateur.getDateEmbauche()));
            
            if (utilisateur.getIdManager() != null) {
                pstmt.setInt(8, utilisateur.getIdManager());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setBoolean(9, utilisateur.isActif());
            pstmt.setInt(10, utilisateur.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'utilisateur #" + utilisateur.getId());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime un utilisateur
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur #" + id);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Authentifie un utilisateur
     */
    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ? AND actif = TRUE";
        
        System.out.println("Tentative d'authentification pour: " + email); // Debug
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = mapResultSetToUtilisateur(rs);
                    System.out.println("Authentification réussie pour: " + user.getEmail() + " - Rôle: " + user.getRole()); // Debug
                    return Optional.of(user);
                } else {
                    System.out.println("Aucun utilisateur trouvé avec ces identifiants"); // Debug
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification");
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    /**
     * Convertit un ResultSet en objet Utilisateur
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setDepartement(rs.getString("departement"));
        
        Date dateEmbauche = rs.getDate("date_embauche");
        if (dateEmbauche != null) {
            user.setDateEmbauche(dateEmbauche.toLocalDate());
        }
        
        int idManager = rs.getInt("id_manager");
        if (!rs.wasNull()) {
            user.setIdManager(idManager);
        }
        
        user.setActif(rs.getBoolean("actif"));
        
        return user;
    }
}
