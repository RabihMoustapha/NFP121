package com.isae.medialibrary.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;
    private Specialty specialty;
    private Set<Media> mediaList = new HashSet<>();

    public Subject(String code, String name, Specialty specialty) {
        this.code = code;
        this.name = name;
        this.specialty = specialty;
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public Specialty getSpecialty() { return specialty; }
    public Set<Media> getMediaList() { return new HashSet<>(mediaList); }
    public void addMedia(Media m) { mediaList.add(m); }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        return code.equals(((Subject) o).code);
    }
    @Override
    public int hashCode() { return code.hashCode(); }
    @Override
    public String toString() { return code + " - " + name; }
}