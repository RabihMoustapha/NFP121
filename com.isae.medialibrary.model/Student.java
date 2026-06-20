package com.isae.medialibrary.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String passwordHash;  // stored as BCrypt hash
    private String nom;
    private String prenom;
    private Specialty specialty;
    private Set<Subject> enrolledSubjects = new HashSet<>();

    public Student(String username, String passwordHash, String nom, String prenom, Specialty specialty) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nom = nom;
        this.prenom = prenom;
        this.specialty = specialty;
    }

    // Getters and setters...
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String hash) { this.passwordHash = hash; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public Specialty getSpecialty() { return specialty; }
    public Set<Subject> getEnrolledSubjects() { return new HashSet<>(enrolledSubjects); }
    public void enrollInSubject(Subject s) { enrolledSubjects.add(s); }

    public boolean isInterestedInMedia(Media media) {
        for (Subject subject : enrolledSubjects) {
            if (media.getSubjects().contains(subject)) return true;
        }
        return false;
    }
    public boolean canEditMedia(Media media) {
        return media.getSubjects().stream().anyMatch(subject -> enrolledSubjects.contains(subject));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return username.equals(((Student) o).username);
    }
    @Override
    public int hashCode() { return username.hashCode(); }
    @Override
    public String toString() { return nom + " " + prenom + " (" + username + ")"; }
}