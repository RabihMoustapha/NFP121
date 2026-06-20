package com.isae.medialibrary.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({DocumentMedia.class, VideoSession.class, OnlineQuiz.class})
public abstract class Media implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String title;
    @XmlAttribute
    private String author;
    @XmlAttribute(name = "year")
    private int publicationYear;
    @XmlAttribute
    private String description;
    @XmlAttribute(name = "accessCount")
    private int accessCount;

    @XmlElement(name = "subject")
    private List<String> subjectCodes = new ArrayList<>();

    @XmlTransient
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

    // Getters and setters...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { title = t; }
    public String getAuthor() { return author; }
    public void setAuthor(String a) { author = a; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int y) { publicationYear = y; }
    public String getDescription() { return description; }
    public void setDescription(String d) { description = d; }
    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int c) { accessCount = c; }
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