package com.isae.medialibrary.service;

import com.isae.medialibrary.model.*;
import com.isae.medialibrary.persistence.XmlManager;
import com.isae.medialibrary.service.filter.*;
import com.isae.medialibrary.util.LogUtil;
import com.isae.medialibrary.exception.*;
import org.slf4j.Logger;

import java.util.*;

public class MediaLibrary {
    private static final Logger logger = LogUtil.getLogger(MediaLibrary.class);

    private Map<String, Media> mediaMap = new HashMap<>();
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Administrator> adminMap = new HashMap<>();
    private Map<String, Specialty> specialtyMap = new HashMap<>();
    private Map<String, Subject> subjectMap = new HashMap<>();
    private NotificationService notificationService = new NotificationService();

    // ========== Persistence ==========
    public void loadData() {
        try {
            XmlManager.loadAllData(this);
        } catch (Exception e) {
            logger.error("Failed to load data", e);
            throw new MediaLibraryException("Could not load data from XML", e);
        }
    }

    public void saveData() {
        try {
            XmlManager.saveAllData(this);
        } catch (Exception e) {
            logger.error("Failed to save data", e);
            throw new MediaLibraryException("Could not save data to XML", e);
        }
    }

    public void clear() {
        mediaMap.clear();
        studentMap.clear();
        adminMap.clear();
        specialtyMap.clear();
        subjectMap.clear();
    }

    // ========== Administrators ==========
    public void addAdministrator(Administrator admin) {
        adminMap.put(admin.getUsername(), admin);
        saveData();
    }

    public Administrator authenticateAdministrator(String username, String password) {
        Administrator admin = adminMap.get(username);
        if (admin != null && PasswordUtil.checkPassword(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }

    public List<Administrator> getAllAdministrators() {
        return new ArrayList<>(adminMap.values());
    }

    // ========== Students ==========
    public void addStudent(Student student) {
        studentMap.put(student.getUsername(), student);
        saveData();
    }

    public Student authenticateStudent(String username, String password) {
        Student student = studentMap.get(username);
        if (student != null && PasswordUtil.checkPassword(password, student.getPassword())) {
            return student;
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentMap.values());
    }

    // ========== Media ==========
    public void addMedia(Media media) {
        if (mediaMap.containsKey(media.getId())) {
            throw new DuplicateIdException("Media with ID " + media.getId() + " already exists.");
        }
        mediaMap.put(media.getId(), media);
        notificationService.notifyStudentsAboutNewMedia(this, media);
        saveData();
    }

    public Media getMedia(String id) {
        Media media = mediaMap.get(id);
        if (media != null) {
            media.incrementAccessCount();
            saveData();
        }
        return media;
    }

    public Media getMediaWithoutIncrement(String id) {
        return mediaMap.get(id);
    }

    public boolean removeMedia(String id) {
        boolean removed = mediaMap.remove(id) != null;
        if (removed) saveData();
        return removed;
    }

    public List<Media> getAllMedia() {
        return new ArrayList<>(mediaMap.values());
    }

    // ========== Specialties ==========
    public void addSpecialty(Specialty specialty) {
        specialtyMap.put(specialty.getNom(), specialty);
    }

    public Specialty getSpecialty(String name) {
        return specialtyMap.get(name);
    }

    public List<Specialty> getAllSpecialties() {
        return new ArrayList<>(specialtyMap.values());
    }

    // ========== Subjects ==========
    public void addSubject(Subject subject) {
        subjectMap.put(subject.getCode(), subject);
    }

    public Subject getSubject(String code) {
        return subjectMap.get(code);
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjectMap.values());
    }

    // ========== Search ==========
    public List<Media> searchMedia(FilterCriteria criteria) {
        List<Media> result = new ArrayList<>();
        for (Media media : mediaMap.values()) {
            if (criteria.matches(media)) result.add(media);
        }
        return result;
    }

    public List<Media> searchByTitle(String title) {
        return searchMedia(new TitleFilter(title));
    }

    public List<Media> searchByAuthor(String author) {
        return searchMedia(new AuthorFilter(author));
    }

    // ========== Reports ==========
    public List<Media> getMostAccessedMedia(int limit) {
        List<Media> all = getAllMedia();
        all.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        int end = Math.min(limit, all.size());
        return all.subList(0, end);
    }

    public List<Media> getMostAccessedBySpecialty(Specialty specialty, int limit) {
        List<Media> filtered = new ArrayList<>();
        for (Media media : mediaMap.values()) {
            for (Subject s : media.getSubjects()) {
                if (s.getSpecialty().equals(specialty)) {
                    filtered.add(media);
                    break;
                }
            }
        }
        filtered.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        int end = Math.min(limit, filtered.size());
        return filtered.subList(0, end);
    }

    public List<Media> getMostAccessedBySubject(Subject subject, int limit) {
        List<Media> filtered = new ArrayList<>();
        for (Media media : mediaMap.values()) {
            if (media.getSubjects().contains(subject)) {
                filtered.add(media);
            }
        }
        filtered.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        int end = Math.min(limit, filtered.size());
        return filtered.subList(0, end);
    }
}