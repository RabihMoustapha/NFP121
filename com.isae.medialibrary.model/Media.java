package com.isae.medialibrary.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Media implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String author;
    private int publicationYear;
    private String description;
    private int accessCount;
    private Set<Subject> subjects = new HashSet<>();

    protected Media(String id, String title, String author, int year, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = year;
        this.description = description;
        this.accessCount = 0;
    }

    public abstract String getType();
    public abstract String getSpecificDetails();

    // Getters and setters (standard)
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getAccessCount() { return accessCount; }
    public void incrementAccessCount() { accessCount++; }
    public Set<Subject> getSubjects() { return new HashSet<>(subjects); }
    public void addSubject(Subject s) { subjects.add(s); }
    public void removeSubject(Subject s) { subjects.remove(s); }
    public void clearSubjects() { subjects.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media)) return false;
        Media media = (Media) o;
        return Objects.equals(id, media.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public String toString() { return title + " (" + id + ")"; }
}