package com.isae.medialibrary.model;

import java.io.Serializable;

public class Administrator implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String passwordHash;
    private String nom;
    private String prenom;

    public Administrator(String username, String passwordHash, String nom, String prenom) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nom = nom;
        this.prenom = prenom;
    }
    // getters/setters...
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String hash) { this.passwordHash = hash; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Administrator)) return false;
        return username.equals(((Administrator) o).username);
    }
    @Override
    public int hashCode() { return username.hashCode(); }
    @Override
    public String toString() { return nom + " " + prenom + " (" + username + ")"; }
}