package com.conges.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.conges.database.DatabaseConnection;
import com.conges.model.TypeConge;

/**
 * DAO pour gérer les types de congés (MySQL)
 */
public class TypeCongeDAO {
    private static TypeCongeDAO instance;
    private Connection connection;
    
    private TypeCongeDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public static TypeCongeDAO getInstance() {
        if (instance == null) {
            instance = new TypeCongeDAO();
        }
        return instance;
    }
    
    public List<TypeConge> getTous() {
        List<TypeConge> typesConge = new ArrayList<>();
        String sql = "SELECT * FROM types_conge ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                typesConge.add(mapResultSetToTypeConge(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des types de congé");
            e.printStackTrace();
        }
        
        return typesConge;
    }
    
    public Optional<TypeConge> getParId(int id) {
        String sql = "SELECT * FROM types_conge WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTypeConge(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du type de congé #" + id);
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public TypeConge ajouter(TypeConge typeConge) {
        String sql = "INSERT INTO types_conge (nom, description, jours_max_par_an, est_paye) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, typeConge.getNom());
            pstmt.setString(2, typeConge.getDescription());
            pstmt.setInt(3, typeConge.getJoursMaxParAn());
            pstmt.setBoolean(4, typeConge.isEstPayé());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        typeConge.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du type de congé");
            e.printStackTrace();
        }
        
        return typeConge;
    }
    
    public boolean modifier(TypeConge typeConge) {
        String sql = "UPDATE types_conge SET nom = ?, description = ?, jours_max_par_an = ?, est_paye = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, typeConge.getNom());
            pstmt.setString(2, typeConge.getDescription());
            pstmt.setInt(3, typeConge.getJoursMaxParAn());
            pstmt.setBoolean(4, typeConge.isEstPayé());
            pstmt.setInt(5, typeConge.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du type de congé #" + typeConge.getId());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimer(int id) {
        String sql = "DELETE FROM types_conge WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du type de congé #" + id);
            e.printStackTrace();
            return false;
        }
    }
    
    private TypeConge mapResultSetToTypeConge(ResultSet rs) throws SQLException {
        TypeConge type = new TypeConge();
        type.setId(rs.getInt("id"));
        type.setNom(rs.getString("nom"));
        type.setDescription(rs.getString("description"));
        type.setJoursMaxParAn(rs.getInt("jours_max_par_an"));
        type.setEstPayé(rs.getBoolean("est_paye"));
        return type;
    }
}
