package com.isae.medialibrary.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Media implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String author;
    private int publicationYear;
    private String description;
    private int accessCount;

    private List<String> subjectCodes = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();

    protected Media() {}
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

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int accessCount) { this.accessCount = accessCount; }
    public void incrementAccessCount() { accessCount++; }
    public List<String> getSubjectCodes() { return subjectCodes; }
    public void setSubjectCodes(List<String> codes) { this.subjectCodes = codes; }
    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }
    public void addSubject(Subject s) { subjects.add(s); subjectCodes.add(s.getCode()); }
    public void removeSubject(Subject s) { subjects.remove(s); subjectCodes.remove(s.getCode()); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media)) return false;
        return id.equals(((Media) o).id);
    }
    @Override
    public int hashCode() { return id.hashCode(); }
    @Override
    public String toString() { return title + " (" + id + ")"; }
}