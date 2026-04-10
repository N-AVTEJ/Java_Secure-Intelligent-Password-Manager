package com.passwordmanager.db;

import com.passwordmanager.model.PasswordEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasswordRepository {
    public List<PasswordEntry> findAll() {
        String sql = "SELECT id, website, username, encrypted_password, category, created_at FROM passwords ORDER BY created_at DESC";
        List<PasswordEntry> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PasswordEntry entry = new PasswordEntry(
                        rs.getInt("id"),
                        rs.getString("website"),
                        rs.getString("username"),
                        rs.getString("encrypted_password"),
                        rs.getString("category"),
                        rs.getTimestamp("created_at")
                );
                result.add(entry);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch password entries. " + rootMessage(e), e);
        }
        return result;
    }

    public void insert(String website, String username, String encryptedPassword, String category) {
        String sql = "INSERT INTO passwords (website, username, encrypted_password, category) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, website);
            ps.setString(2, username);
            ps.setString(3, encryptedPassword);
            ps.setString(4, category);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert password entry. " + rootMessage(e), e);
        }
    }

    public void upsert(String website, String username, String encryptedPassword, String category) {
        String selectSql = "SELECT id FROM passwords WHERE website = ? AND username = ? AND category = ? LIMIT 1";
        String updateSql = "UPDATE passwords SET encrypted_password = ? WHERE id = ?";
        String insertSql = "INSERT INTO passwords (website, username, encrypted_password, category) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement psSelect = connection.prepareStatement(selectSql)) {
                psSelect.setString(1, website);
                psSelect.setString(2, username);
                psSelect.setString(3, category);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        try (PreparedStatement psUpdate = connection.prepareStatement(updateSql)) {
                            psUpdate.setString(1, encryptedPassword);
                            psUpdate.setInt(2, id);
                            psUpdate.executeUpdate();
                        }
                        return;
                    }
                }
            }

            try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                psInsert.setString(1, website);
                psInsert.setString(2, username);
                psInsert.setString(3, encryptedPassword);
                psInsert.setString(4, category);
                psInsert.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save password entry. " + e.getMessage(), e);
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM passwords WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete password entry. " + rootMessage(e), e);
        }
    }

    private String rootMessage(Exception e) {
        Throwable current = e;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        if (current instanceof SQLException sqlEx && sqlEx.getMessage() != null) {
            return sqlEx.getMessage();
        }
        return current.getMessage() == null ? "Unknown database error." : current.getMessage();
    }

    public void deleteAll() {
        String sql = "DELETE FROM passwords";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all password entries. " + e.getMessage(), e);
        }
    }
}
