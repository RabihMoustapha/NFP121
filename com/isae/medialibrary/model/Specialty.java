package com.isae.medialibrary.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Specialty implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private List<Student> students = new ArrayList<>();

    public Specialty() {}
    public Specialty(String nom) { this.nom = nom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialty)) return false;
        return nom.equals(((Specialty) o).nom);
    }
    @Override
    public int hashCode() { return nom.hashCode(); }
    @Override
    public String toString() { return nom; }
}