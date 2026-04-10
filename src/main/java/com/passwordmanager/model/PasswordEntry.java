package com.passwordmanager.model;

import java.sql.Timestamp;

public class PasswordEntry {
    private int id;
    private String website;
    private String username;
    private String encryptedPassword;
    private String category;
    private Timestamp createdAt;

    public PasswordEntry() {
    }

    public PasswordEntry(int id, String website, String username, String encryptedPassword, String category, Timestamp createdAt) {
        this.id = id;
        this.website = website;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.category = category;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
