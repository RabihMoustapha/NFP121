import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


class MediaLibrary extends Observable {
    private Map<String, Media> mediaMap = new HashMap<>();
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Specialty> specialtyMap = new HashMap<>();
    private Map<String, Subject> subjectMap = new HashMap<>();
    private Map<String, Administrator> adminMap = new HashMap<>();

    // Media operations
    public void addMedia(Media media) {
        mediaMap.put(media.getId(), media);
        notifyObservers(media);
    }

    public Media getMedia(String id) {
        Media media = mediaMap.get(id);
        if (media != null)
            media.incrementAccessCount();
        return media;
    }

    public boolean removeMedia(String id) {
        return mediaMap.remove(id) != null;
    }

    public List<Media> getAllMedia() {
        return new ArrayList<>(mediaMap.values());
    }

    public List<Media> searchMedia(FilterCriteria criteria) {
        List<Media> result = new ArrayList<>();
        for (Media media : mediaMap.values()) {
            if (criteria.matches(media))
                result.add(media);
        }
        return result;
    }

    public List<Media> searchByTitle(String title) {
        return searchMedia(new TitleFilter(title));
    }

    // Student operations
    public void addStudent(Student student) {
        studentMap.put(student.getUsername(), student);
        registerObserver(student);
    }

    public Student authenticateStudent(String username, String password) {
        Student student = studentMap.get(username);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentMap.values());
    }

    // Administrator operations
    public void addAdministrator(Administrator admin) {
        adminMap.put(admin.getUsername(), admin);
    }

    public Administrator authenticateAdministrator(String username, String password) {
        Administrator admin = adminMap.get(username);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    public List<Administrator> getAllAdministrators() {
        return new ArrayList<>(adminMap.values());
    }

    // Specialty/Subject operations
    public void addSpecialty(Specialty specialty) {
        specialtyMap.put(specialty.getName(), specialty);
    }

    public void addSubject(Subject subject) {
        subjectMap.put(subject.getCode(), subject);
    }

    public Specialty getSpecialty(String name) {
        return specialtyMap.get(name);
    }

    public List<Specialty> getAllSpecialties() {
        return new ArrayList<>(specialtyMap.values());
    }

    public Subject getSubject(String code) {
        return subjectMap.get(code);
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjectMap.values());
    }

    // XML operations
    public void loadAllDataFromXML() throws Exception {
        UniversityXMLManager.loadAllData(this);
    }

    public void saveAllDataToXML() throws Exception {
        UniversityXMLManager.saveAllData(this);
    }

    // Statistics
    public List<Media> getMostAccessedMedia(int limit) {
        List<Media> all = new ArrayList<>(mediaMap.values());
        all.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        return all.subList(0, Math.min(limit, all.size()));
    }

    public List<Media> getMostAccessedBySpecialty(Specialty specialty, int limit) {
        Set<Subject> specialtySubjects = specialty.getSubjects();
        List<Media> result = new ArrayList<>();

        for (Media media : mediaMap.values()) {
            for (Subject subject : media.getSubjects()) {
                if (specialtySubjects.contains(subject)) {
                    result.add(media);
                    break;
                }
            }
        }

        result.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        return result.subList(0, Math.min(limit, result.size()));
    }

    public List<Media> getMostAccessedBySubject(Subject subject, int limit) {
        List<Media> result = new ArrayList<>();
        for (Media media : mediaMap.values()) {
            if (media.getSubjects().contains(subject)) {
                result.add(media);
            }
        }

        result.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        return result.subList(0, Math.min(limit, result.size()));
    }

    // Persistence
    public void saveToBinary(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            LibraryData data = new LibraryData(
                    new ArrayList<>(mediaMap.values()),
                    new ArrayList<>(studentMap.values()),
                    new ArrayList<>(specialtyMap.values()),
                    new ArrayList<>(subjectMap.values()),
                    new ArrayList<>(adminMap.values()));
            oos.writeObject(data);
        }
    }

    public void loadFromBinary(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            LibraryData data = (LibraryData) ois.readObject();

            mediaMap.clear();
            for (Media m : data.mediaList)
                mediaMap.put(m.getId(), m);

            studentMap.clear();
            for (Student s : data.students) {
                studentMap.put(s.getUsername(), s);
                registerObserver(s);
            }

            specialtyMap.clear();
            for (Specialty sp : data.specialties)
                specialtyMap.put(sp.getName(), sp);

            subjectMap.clear();
            for (Subject su : data.subjects)
                subjectMap.put(su.getCode(), su);

            adminMap.clear();
            for (Administrator a : data.administrators)
                adminMap.put(a.getUsername(), a);
        }
    }

    // Helper class for serialization
    private static class LibraryData implements Serializable {
        private static final long serialVersionUID = 1L;
        List<Media> mediaList;
        List<Student> students;
        List<Specialty> specialties;
        List<Subject> subjects;
        List<Administrator> administrators;

        public LibraryData(List<Media> ml, List<Student> st, List<Specialty> sp, 
                          List<Subject> su, List<Administrator> adm) {
            mediaList = ml;
            students = st;
            specialties = sp;
            subjects = su;
            administrators = adm;
        }
    }
}