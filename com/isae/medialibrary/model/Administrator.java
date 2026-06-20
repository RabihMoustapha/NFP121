package com.isae.medialibrary.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class Administrator implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private String username;
    @XmlAttribute
    private String password; // BCrypt hash
    @XmlAttribute
    private String nom;
    @XmlAttribute
    private String prenom;

    public Administrator() {}

    public Administrator(String username, String passwordHash, String nom, String prenom) {
        this.username = username;
        this.password = passwordHash;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

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