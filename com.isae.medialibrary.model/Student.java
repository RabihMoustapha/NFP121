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
    private String password; // BCrypt hash
    private String nom;
    private String prenom;

    @XmlTransient
    private Specialty specialty;

    @XmlElement(name = "valeur")
    private List<String> subjectCodes = new ArrayList<>();

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

    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
    public String getNom() { return nom; }
    public void setNom(String n) { nom = n; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String p) { prenom = p; }
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty s) { specialty = s; }
    public List<String> getSubjectCodes() { return subjectCodes; }
    public void setSubjectCodes(List<String> codes) { this.subjectCodes = codes; }
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