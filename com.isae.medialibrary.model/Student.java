package com.isae.medialibrary.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private String username;
    @XmlAttribute
    private String password; // will store hash
    private String nom;
    private String prenom;

    // JAXB: we'll skip specialty reference via parent; but we can keep a transient reference.
    @XmlTransient
    private Specialty specialty;

    @XmlElement(name = "valeur")
    private List<String> subjectCodes = new ArrayList<>();

    // transient enrolled subjects (populated after loading)
    @XmlTransient
    private List<Subject> enrolledSubjects = new ArrayList<>();

    public Student() {}
    public Student(String username, String passwordHash, String nom, String prenom, Specialty specialty) {
        this.username = username;
        this.password = passwordHash;
        this.nom = nom;
        this.prenom = prenom;
        this.specialty = specialty;
    }

    // Getters and setters...
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }
    public List<String> getSubjectCodes() { return subjectCodes; }
    public void setSubjectCodes(List<String> subjectCodes) { this.subjectCodes = subjectCodes; }
    public List<Subject> getEnrolledSubjects() { return enrolledSubjects; }
    public void setEnrolledSubjects(List<Subject> subjects) { this.enrolledSubjects = subjects; }

    public boolean isInterestedInMedia(Media media) {
        for (Subject s : enrolledSubjects) {
            if (media.getSubjects().contains(s)) return true;
        }
        return false;
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
    public String toString() { return nom + " " + prenom; }
}