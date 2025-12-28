import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// ==================== INTERFACES ====================

interface MediaFactory {
    Media createMedia(String id, String title, String author, int year, String description, Object... params);
}

interface FilterCriteria {
    boolean matches(Media media);
}

interface Exporter {
    void export(List<Media> mediaList, String filePath) throws Exception;
}

interface Observer {
    void update(Object updateInfo);
}

interface StatisticsReport {
    String generateReport(MediaLibrary lib);
}

// ==================== ABSTRACT CLASSES ====================

abstract class Media implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String author;
    private int publicationYear;
    private String description;
    private int accessCount;
    private Set<Subject> subjects;

    public Media(String id, String title, String author, int year, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = year;
        this.description = description;
        this.accessCount = 0;
        this.subjects = new HashSet<>();
    }

    public abstract String getType();

    public abstract String getSpecificDetails();

    // Getters et setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String a) {
        author = a;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int y) {
        publicationYear = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void incrementAccessCount() {
        accessCount++;
    }

    public Set<Subject> getSubjects() {
        return new HashSet<>(subjects);
    }

    public void addSubject(Subject s) {
        subjects.add(s);
    }

    public void removeSubject(Subject s) {
        subjects.remove(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Media))
            return false;
        return id.equals(((Media) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

abstract class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer o) {
        if (!observers.contains(o))
            observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(Object info) {
        for (Observer o : observers)
            o.update(info);
    }
}

// ==================== CONCRETE MEDIA CLASSES ====================

class DocumentMedia extends Media {
    private int pageCount;

    public DocumentMedia(String id, String title, String author, int year, String desc, int pages) {
        super(id, title, author, year, desc);
        this.pageCount = pages;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int p) {
        pageCount = p;
    }

    @Override
    public String getType() {
        return "Document";
    }

    @Override
    public String getSpecificDetails() {
        return "Pages: " + pageCount;
    }
}

class VideoSession extends Media {
    private int durationMinutes;

    public VideoSession(String id, String title, String author, int year, String desc, int duration) {
        super(id, title, author, year, desc);
        this.durationMinutes = duration;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int d) {
        durationMinutes = d;
    }

    @Override
    public String getType() {
        return "Video Session";
    }

    @Override
    public String getSpecificDetails() {
        return "Duration: " + durationMinutes + " minutes";
    }
}

class OnlineQuiz extends Media {
    private int estimatedDuration;
    private String difficultyLevel;

    public OnlineQuiz(String id, String title, String author, int year, String desc,
            int duration, String difficulty) {
        super(id, title, author, year, desc);
        this.estimatedDuration = duration;
        this.difficultyLevel = difficulty;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int d) {
        estimatedDuration = d;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String d) {
        difficultyLevel = d;
    }

    @Override
    public String getType() {
        return "Online Quiz";
    }

    @Override
    public String getSpecificDetails() {
        return "Duration: " + estimatedDuration + " minutes, Difficulty: " + difficultyLevel;
    }
}

// ==================== DOMAIN CLASSES ====================

class Subject implements Serializable {
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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public Set<Media> getMediaList() {
        return new HashSet<>(mediaList);
    }

    public void addMedia(Media m) {
        mediaList.add(m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Subject))
            return false;
        return code.equals(((Subject) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}

class Specialty implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Set<Subject> subjects = new HashSet<>();

    public Specialty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Subject> getSubjects() {
        return new HashSet<>(subjects);
    }

    public void addSubject(Subject s) {
        subjects.add(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Specialty))
            return false;
        return name.equals(((Specialty) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}

class Student implements Observer, Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private Specialty specialty;
    private Set<Subject> enrolledSubjects = new HashSet<>();

    public Student(String username, String password, String nom, String prenom, Specialty specialty) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.specialty = specialty;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public Set<Subject> getEnrolledSubjects() {
        return new HashSet<>(enrolledSubjects);
    }

    public void enrollInSubject(Subject s) {
        enrolledSubjects.add(s);
    }

    public boolean isInterestedInMedia(Media media) {
        for (Subject subject : enrolledSubjects) {
            if (media.getSubjects().contains(subject))
                return true;
        }
        return false;
    }

    @Override
    public void update(Object info) {
        if (info instanceof Media) {
            Media newMedia = (Media) info;
            if (isInterestedInMedia(newMedia)) {
                System.out.println("EMAIL to " + username + ": New media '" + newMedia.getTitle() + "' available");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Student))
            return false;
        return username.equals(((Student) o).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + username + ") - " + specialty.getName();
    }
}

// ==================== ADMINISTRATOR CLASS ====================

class Administrator implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private String email;

    public Administrator(String username, String password, String nom, String prenom, String email) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Administrator))
            return false;
        return username.equals(((Administrator) o).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + username + ") - " + email;
    }
}

// ==================== FACTORY CLASSES ====================

class DocumentMediaFactory implements MediaFactory {
    @Override
    public Media createMedia(String id, String title, String author, int year,
            String desc, Object... params) {
        if (params.length < 1)
            throw new IllegalArgumentException("Need page count");
        return new DocumentMedia(id, title, author, year, desc, (Integer) params[0]);
    }
}

class VideoFactory implements MediaFactory {
    @Override
    public Media createMedia(String id, String title, String author, int year,
            String desc, Object... params) {
        if (params.length < 1)
            throw new IllegalArgumentException("Need duration");
        return new VideoSession(id, title, author, year, desc, (Integer) params[0]);
    }
}

class QuizFactory implements MediaFactory {
    @Override
    public Media createMedia(String id, String title, String author, int year,
            String desc, Object... params) {
        if (params.length < 2)
            throw new IllegalArgumentException("Need duration and difficulty");
        return new OnlineQuiz(id, title, author, year, desc,
                (Integer) params[0], (String) params[1]);
    }
}

class MediaFactoryRegistry {
    private static MediaFactoryRegistry instance;
    private Map<String, MediaFactory> factories = new HashMap<>();

    private MediaFactoryRegistry() {
        registerFactory("document", new DocumentMediaFactory());
        registerFactory("video", new VideoFactory());
        registerFactory("quiz", new QuizFactory());
    }

    public static MediaFactoryRegistry getInstance() {
        if (instance == null)
            instance = new MediaFactoryRegistry();
        return instance;
    }

    public void registerFactory(String type, MediaFactory factory) {
        factories.put(type.toLowerCase(), factory);
    }

    public MediaFactory getFactory(String type) {
        MediaFactory factory = factories.get(type.toLowerCase());
        if (factory == null)
            throw new IllegalArgumentException("Unknown type: " + type);
        return factory;
    }
}

// ==================== FILTER CLASSES ====================

class AuthorFilter implements FilterCriteria {
    private String author;

    public AuthorFilter(String author) {
        this.author = author.toLowerCase();
    }

    @Override
    public boolean matches(Media media) {
        return media.getAuthor().toLowerCase().contains(author);
    }
}

class SubjectFilter implements FilterCriteria {
    private Subject subject;

    public SubjectFilter(Subject subject) {
        this.subject = subject;
    }

    @Override
    public boolean matches(Media media) {
        return media.getSubjects().contains(subject);
    }
}

class TitleFilter implements FilterCriteria {
    private String title;

    public TitleFilter(String title) {
        this.title = title.toLowerCase();
    }

    @Override
    public boolean matches(Media media) {
        return media.getTitle().toLowerCase().contains(title);
    }
}

class FilterComposite implements FilterCriteria {
    public enum Operator {
        AND, OR
    }

    private List<FilterCriteria> criteria = new ArrayList<>();
    private Operator operator;

    public FilterComposite(Operator operator) {
        this.operator = operator;
    }

    public void addCriterion(FilterCriteria c) {
        criteria.add(c);
    }

    @Override
    public boolean matches(Media media) {
        if (criteria.isEmpty())
            return true;

        if (operator == Operator.AND) {
            for (FilterCriteria c : criteria) {
                if (!c.matches(media))
                    return false;
            }
            return true;
        } else { // OR
            for (FilterCriteria c : criteria) {
                if (c.matches(media))
                    return true;
            }
            return false;
        }
    }
}

// ==================== EXPORTER CLASSES ====================

class XMLExporter implements Exporter {
    @Override
    public void export(List<Media> mediaList, String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();

        Element root = doc.createElement("mediaLibrary");
        doc.appendChild(root);

        for (Media media : mediaList) {
            Element mediaElem = doc.createElement("media");
            mediaElem.setAttribute("id", media.getId());
            mediaElem.setAttribute("type", media.getType());

            addElement(doc, mediaElem, "title", media.getTitle());
            addElement(doc, mediaElem, "author", media.getAuthor());
            addElement(doc, mediaElem, "year", String.valueOf(media.getPublicationYear()));
            addElement(doc, mediaElem, "description", media.getDescription());
            addElement(doc, mediaElem, "accessCount", String.valueOf(media.getAccessCount()));

            Element subjectsElem = doc.createElement("subjects");
            for (Subject subject : media.getSubjects()) {
                Element subjElem = doc.createElement("subject");
                subjElem.setTextContent(subject.getCode());
                subjectsElem.appendChild(subjElem);
            }
            mediaElem.appendChild(subjectsElem);
            root.appendChild(mediaElem);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    private void addElement(org.w3c.dom.Document doc, Element parent, String name, String value) {
        Element elem = doc.createElement(name);
        elem.setTextContent(value);
        parent.appendChild(elem);
    }
}

class CSVExporter implements Exporter {
    @Override
    public void export(List<Media> mediaList, String filePath) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Title,Author,Year,Type,AccessCount,Subjects");
            for (Media media : mediaList) {
                String subjects = "";
                for (Subject subject : media.getSubjects()) {
                    subjects += subject.getCode() + ";";
                }
                if (!subjects.isEmpty())
                    subjects = subjects.substring(0, subjects.length() - 1);

                writer.printf("\"%s\",\"%s\",\"%s\",%d,\"%s\",%d,\"%s\"%n",
                        media.getId(),
                        media.getTitle(),
                        media.getAuthor(),
                        media.getPublicationYear(),
                        media.getType(),
                        media.getAccessCount(),
                        subjects);
            }
        }
    }
}

// ==================== XML DATA IMPORTER/EXPORTER ====================

class UniversityXMLManager {
    private static final String XML_FILE = "universite.xml";
    
    // Load all data from XML
    public static void loadAllData(MediaLibrary library) throws Exception {
        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            System.out.println("XML file not found. Creating default file...");
            createDefaultXMLFile();
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(xmlFile);

        // Load administrators
        NodeList adminNodes = doc.getElementsByTagName("administrateur");
        for (int i = 0; i < adminNodes.getLength(); i++) {
            org.w3c.dom.Element adminElem = (org.w3c.dom.Element) adminNodes.item(i);
            String username = adminElem.getAttribute("username");
            String password = adminElem.getAttribute("password");
            String email = username; // Username is the email in your format
            
            // Extract name from email
            String emailPart = username.split("@")[0];
            String nom = emailPart.contains(".") ? 
                emailPart.substring(emailPart.indexOf(".") + 1) : emailPart;
            String prenom = emailPart.contains(".") ? 
                emailPart.substring(0, emailPart.indexOf(".")) : "Admin";
            
            Administrator admin = new Administrator(username, password, 
                capitalize(nom), capitalize(prenom), email);
            library.addAdministrator(admin);
        }

        // Load specialties and students
        NodeList specialites = doc.getElementsByTagName("specialite");
        for (int i = 0; i < specialites.getLength(); i++) {
            org.w3c.dom.Element specElem = (org.w3c.dom.Element) specialites.item(i);
            String specName = specElem.getAttribute("nom");
            
            // Create or retrieve specialty
            Specialty specialty = library.getSpecialty(specName);
            if (specialty == null) {
                specialty = new Specialty(specName);
                library.addSpecialty(specialty);
            }

            NodeList etudiants = specElem.getElementsByTagName("etudiant");
            for (int j = 0; j < etudiants.getLength(); j++) {
                org.w3c.dom.Element etudElem = (org.w3c.dom.Element) etudiants.item(j);
                String username = etudElem.getAttribute("username");
                String password = etudElem.getAttribute("password");
                
                // Check if student already exists
                boolean studentExists = library.getAllStudents().stream()
                    .anyMatch(s -> s.getUsername().equals(username));
                
                if (!studentExists) {
                    // Extract name from email/username
                    String[] nameParts = username.split("@")[0].split("\\.");
                    String prenom = nameParts.length > 0 ? capitalize(nameParts[0]) : "Unknown";
                    String nom = nameParts.length > 1 ? capitalize(nameParts[1]) : "Unknown";
                    
                    Student student = new Student(username, password, nom, prenom, specialty);
                    
                    // Add subjects
                    NodeList valeurs = etudElem.getElementsByTagName("valeur");
                    for (int k = 0; k < valeurs.getLength(); k++) {
                        String subjectCode = valeurs.item(k).getTextContent().trim();
                        
                        // Create subject if it doesn't exist
                        Subject subject = library.getSubject(subjectCode);
                        if (subject == null) {
                            subject = new Subject(subjectCode, subjectCode, specialty);
                            library.addSubject(subject);
                            specialty.addSubject(subject);
                        }
                        
                        student.enrollInSubject(subject);
                    }
                    
                    library.addStudent(student);
                }
            }
        }
    }
    
    // Save all data to XML
    public static void saveAllData(MediaLibrary library) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();

        // Create root element
        Element root = doc.createElement("issae");
        doc.appendChild(root);

        // Save administrators
        Element adminSection = doc.createElement("administrateurs");
        for (Administrator admin : library.getAllAdministrators()) {
            Element adminElem = doc.createElement("administrateur");
            adminElem.setAttribute("username", admin.getUsername());
            adminElem.setAttribute("password", admin.getPassword());
            adminSection.appendChild(adminElem);
        }
        root.appendChild(adminSection);

        // Organize students by specialty
        Map<Specialty, List<Student>> studentsBySpecialty = new HashMap<>();
        for (Student student : library.getAllStudents()) {
            Specialty specialty = student.getSpecialty();
            studentsBySpecialty.computeIfAbsent(specialty, k -> new ArrayList<>()).add(student);
        }

        // Save specialties and students
        for (Map.Entry<Specialty, List<Student>> entry : studentsBySpecialty.entrySet()) {
            Specialty specialty = entry.getKey();
            List<Student> students = entry.getValue();
            
            Element specialiteElem = doc.createElement("specialite");
            specialiteElem.setAttribute("nom", specialty.getName());
            root.appendChild(specialiteElem);

            for (Student student : students) {
                Element etudiantElem = doc.createElement("etudiant");
                etudiantElem.setAttribute("username", student.getUsername());
                etudiantElem.setAttribute("password", student.getPassword());
                specialiteElem.appendChild(etudiantElem);

                for (Subject subject : student.getEnrolledSubjects()) {
                    Element valeurElem = doc.createElement("valeur");
                    valeurElem.appendChild(doc.createTextNode(subject.getCode()));
                    etudiantElem.appendChild(valeurElem);
                }
            }
        }

        // Write to file
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(XML_FILE));
        transformer.transform(source, result);
    }
    
    private static void createDefaultXMLFile() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();

        Element root = doc.createElement("issae");
        doc.appendChild(root);

        // Add default administrator
        Element adminSection = doc.createElement("administrateurs");
        Element adminElem = doc.createElement("administrateur");
        adminElem.setAttribute("username", "admin");
        adminElem.setAttribute("password", "admin");
        adminSection.appendChild(adminElem);
        root.appendChild(adminSection);

        // Add default specialties
        String[] specialties = {"Informatique", "Mathematiques", "Physique"};
        for (String specName : specialties) {
            Element specialiteElem = doc.createElement("specialite");
            specialiteElem.setAttribute("nom", specName);
            root.appendChild(specialiteElem);
        }

        // Write to file
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(XML_FILE));
        transformer.transform(source, result);
    }
    
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

// ==================== STATISTICS CLASSES ====================

class MostAccessedBySpecialtyReport implements StatisticsReport {
    private String specialtyName;

    public MostAccessedBySpecialtyReport(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    @Override
    public String generateReport(MediaLibrary lib) {
        StringBuilder sb = new StringBuilder();
        sb.append("Most Accessed Media for Specialty: ").append(specialtyName).append("\n");
        sb.append("==========================================\n");

        Specialty spec = lib.getSpecialty(specialtyName);
        if (spec == null)
            return "Specialty not found";

        List<Media> topMedia = lib.getMostAccessedBySpecialty(spec, 10);
        for (int i = 0; i < topMedia.size(); i++) {
            Media m = topMedia.get(i);
            sb.append(String.format("%d. %s (ID: %s) - %d accesses%n",
                    i + 1, m.getTitle(), m.getId(), m.getAccessCount()));
        }

        return sb.toString();
    }
}

// ==================== MAIN LIBRARY CLASS ====================

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

// ==================== GUI CLASSES ====================

class StudentLoginFrame extends JFrame {
    private MediaLibrary library;
    private JTextField userField;
    private JPasswordField passField;

    public StudentLoginFrame(MediaLibrary lib) {
        this.library = lib;
        setTitle("Student Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        formPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        formPanel.add(userField);
        
        formPanel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        formPanel.add(passField);
        
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        
        formPanel.add(loginBtn);
        formPanel.add(cancelBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Create account link panel at bottom
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel createAccountLabel = new JLabel("Create account");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add underline effect using HTML
        createAccountLabel.setText("<html><u>Create account</u></html>");
        
        linkPanel.add(createAccountLabel);
        mainPanel.add(linkPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // Action listeners
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            Student student = library.authenticateStudent(username, password);
            if (student != null) {
                dispose();
                new StudentMainFrame(library, student).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        // Create account link action
        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NewStudentFrame newStudentFrame = new NewStudentFrame(library);
                newStudentFrame.setVisible(true);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createAccountLabel.setForeground(new Color(0, 100, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createAccountLabel.setForeground(Color.BLUE);
            }
        });

        setLocationRelativeTo(null);
    }
}

class StudentMainFrame extends JFrame {
    private MediaLibrary library;
    private Student student;
    private JTable mediaTable;
    private DefaultTableModel tableModel;

    public StudentMainFrame(MediaLibrary lib, Student stud) {
        this.library = lib;
        this.student = stud;

        setTitle("Media Library - Student: " + stud.getNom() + " " + stud.getPrenom());
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Student info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Student: " + stud.getNom() + " " + stud.getPrenom()));
        infoPanel.add(new JLabel("Specialty: " + stud.getSpecialty().getName()));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JComboBox<String> filterCombo = new JComboBox<>(new String[] { "Title", "Author" });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(filterCombo);
        searchPanel.add(searchBtn);

        // Media table
        String[] columns = { "ID", "Title", "Author", "Year", "Type", "Accesses" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mediaTable);

        // Details area
        JTextArea detailsArea = new JTextArea(5, 60);
        detailsArea.setEditable(false);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton viewBtn = new JButton("View Media");
        JButton filterBtn = new JButton("My Subjects");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(viewBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(logoutBtn);

        // Layout
        setLayout(new BorderLayout(5, 5));
        add(infoPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(detailsScroll, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadAllMedia();

        // Event listeners
        viewBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMedia(id);
                if (media != null) {
                    detailsArea.setText("Title: " + media.getTitle() + "\n" +
                            "Author: " + media.getAuthor() + "\n" +
                            "Description: " + media.getDescription() + "\n" +
                            "Type: " + media.getType() + "\n" +
                            media.getSpecificDetails());
                    JOptionPane.showMessageDialog(this, "Media accessed. Count incremented.");
                }
            }
        });

        filterBtn.addActionListener(e -> {
            FilterComposite filter = new FilterComposite(FilterComposite.Operator.OR);
            for (Subject subject : student.getEnrolledSubjects()) {
                filter.addCriterion(new SubjectFilter(subject));
            }
            displayMedia(library.searchMedia(filter));
        });

        searchBtn.addActionListener(e -> {
            String query = searchField.getText();
            String filterType = (String) filterCombo.getSelectedItem();

            if (query.isEmpty()) {
                loadAllMedia();
                return;
            }

            FilterCriteria criteria;
            if ("Author".equals(filterType)) {
                criteria = new AuthorFilter(query);
            } else {
                criteria = new TitleFilter(query);
            }

            displayMedia(library.searchMedia(criteria));
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new StudentLoginFrame(library).setVisible(true);
        });

        setLocationRelativeTo(null);
    }

    private void loadAllMedia() {
        displayMedia(library.getAllMedia());
    }

    private void displayMedia(List<Media> mediaList) {
        tableModel.setRowCount(0);
        for (Media media : mediaList) {
            tableModel.addRow(new Object[] {
                    media.getId(),
                    media.getTitle(),
                    media.getAuthor(),
                    media.getPublicationYear(),
                    media.getType(),
                    media.getAccessCount()
            });
        }
    }
}

class NewStudentFrame extends JFrame {
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JComboBox<String> specialiteCombo = new JComboBox<>();
    private JComboBox<String> valeurCombo = new JComboBox<>(new String[]{"NFA032", "NFA035", "NFP121"});
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private MediaLibrary library;
    
    public NewStudentFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Student");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Initialiser les spécialités disponibles
        updateSpecialiteCombo();
        
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prenom:"));
        form.add(prenomField);
        form.add(new JLabel("Specialite:"));
        form.add(specialiteCombo);
        form.add(new JLabel("Valeur:"));
        form.add(valeurCombo);
        form.add(new JLabel("Username (email):"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Action listeners
        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> this.dispose());
        
        // Auto-generate username
        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
    }
    
    private void updateSpecialiteCombo() {
        specialiteCombo.removeAllItems();
        // Récupérer les spécialités de la bibliothèque
        for (Specialty specialty : library.getAllSpecialties()) {
            specialiteCombo.addItem(specialty.getName());
        }
        // Si vide, ajouter des spécialités par défaut
        if (specialiteCombo.getItemCount() == 0) {
            specialiteCombo.addItem("Informatique");
            specialiteCombo.addItem("Mathematiques");
            specialiteCombo.addItem("Physique");
        }
    }
    
    private void generateUsername() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            String username = prenom.toLowerCase() + "." + nom.toLowerCase() + "@isae.edu.lb";
            usernameField.setText(username);
        }
    }
    
    private void saveStudent() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String specialite = (String) specialiteCombo.getSelectedItem();
        String valeur = (String) valeurCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Vérifier si l'username existe déjà (étudiant ou administrateur)
        if (library.authenticateStudent(username, password) != null || 
            library.getAllStudents().stream().anyMatch(s -> s.getUsername().equals(username)) ||
            library.getAllAdministrators().stream().anyMatch(a -> a.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Créer ou récupérer la spécialité
        Specialty specialtyObj = library.getSpecialty(specialite);
        if (specialtyObj == null) {
            specialtyObj = new Specialty(specialite);
            library.addSpecialty(specialtyObj);
        }
        
        // Créer ou récupérer le sujet
        Subject subject = library.getSubject(valeur);
        if (subject == null) {
            subject = new Subject(valeur, valeur + " - " + specialite, specialtyObj);
            library.addSubject(subject);
            specialtyObj.addSubject(subject);
        }
        
        // Créer l'étudiant
        Student student = new Student(username, password, nom, prenom, specialtyObj);
        student.enrollInSubject(subject);
        library.addStudent(student);
        
        try {
            // Sauvegarder automatiquement dans le fichier universite.xml
            library.saveAllDataToXML();
            
            JOptionPane.showMessageDialog(this, 
                "Student added and saved to universite.xml successfully!\n\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "Auto-saved to: universite.xml",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Student added but XML save failed: " + ex.getMessage() + "\n" +
                "Data is only in memory. Please export manually.",
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
        
        this.dispose();
    }
}

class AdminLoginFrame extends JFrame {
    private MediaLibrary library;
    private JTextField userField;
    private JPasswordField passField;

    public AdminLoginFrame(MediaLibrary lib) {
        this.library = lib;
        setTitle("Admin Login");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        formPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        formPanel.add(userField);
        
        formPanel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        formPanel.add(passField);
        
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        
        formPanel.add(loginBtn);
        formPanel.add(cancelBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Link panel at bottom
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel createAccountLabel = new JLabel("<html><u>Create Admin Account</u></html>");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.setToolTipText("Click to create a new admin account");
        
        linkPanel.add(createAccountLabel);
        mainPanel.add(linkPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // Action listeners
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            Administrator admin = library.authenticateAdministrator(username, password);
            if (admin != null) {
                dispose();
                new AdminMainFrame(library).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials");
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        // Create account action (opens student creation)
        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NewStudentFrame newStudentFrame = new NewStudentFrame(library);
                newStudentFrame.setVisible(true);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createAccountLabel.setForeground(new Color(0, 100, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createAccountLabel.setForeground(Color.BLUE);
            }
        });

        setLocationRelativeTo(null);
    }
}

class AdminMainFrame extends JFrame {
    private MediaLibrary library;
    private JTable mediaTable;
    private DefaultTableModel tableModel;
    private JButton addStudentBtn;

    public AdminMainFrame(MediaLibrary lib) {
        this.library = lib;

        setTitle("Media Library - Admin Panel");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(4, 5, 5, 5));

        JButton addBtn = new JButton("Add Media");
        JButton deleteBtn = new JButton("Delete Media");
        addStudentBtn = new JButton("Add Student");
        JButton manageSubjectsBtn = new JButton("Manage Subjects");
        
        JButton saveXmlBtn = new JButton("Save to XML");
        JButton loadXmlBtn = new JButton("Reload from XML");
        
        JButton exportXmlBtn = new JButton("Export Media XML");
        JButton exportCsvBtn = new JButton("Export Media CSV");
        JButton statsBtn = new JButton("Media Stats");
        JButton studentStatsBtn = new JButton("Student Stats");
        
        JButton saveBtn = new JButton("Save Binary");
        JButton loadBtn = new JButton("Load Binary");
        JButton viewStudentsBtn = new JButton("View Students");
        JButton viewAdminsBtn = new JButton("View Admins");
        JButton addAdminBtn = new JButton("Add Admin");
        JButton logoutBtn = new JButton("Logout");

        // Ajouter les boutons au panel
        controlPanel.add(addBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(addStudentBtn);
        controlPanel.add(manageSubjectsBtn);
        controlPanel.add(saveXmlBtn);
        
        controlPanel.add(loadXmlBtn);
        controlPanel.add(exportXmlBtn);
        controlPanel.add(exportCsvBtn);
        controlPanel.add(statsBtn);
        controlPanel.add(studentStatsBtn);
        
        controlPanel.add(saveBtn);
        controlPanel.add(loadBtn);
        controlPanel.add(viewStudentsBtn);
        controlPanel.add(viewAdminsBtn);
        controlPanel.add(addAdminBtn);
        controlPanel.add(logoutBtn);

        // Media table
        String[] columns = { "ID", "Title", "Author", "Year", "Type", "Accesses" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mediaTable);

        // Layout
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadMediaData();

        // Event listeners
        addBtn.addActionListener(e -> showAddMediaDialog());
        deleteBtn.addActionListener(e -> deleteSelectedMedia());
        addStudentBtn.addActionListener(e -> showAddStudentDialog());
        manageSubjectsBtn.addActionListener(e -> showManageSubjectsDialog());
        saveXmlBtn.addActionListener(e -> saveToXML());
        loadXmlBtn.addActionListener(e -> loadFromXML());
        exportXmlBtn.addActionListener(e -> exportMedia("XML"));
        exportCsvBtn.addActionListener(e -> exportMedia("CSV"));
        statsBtn.addActionListener(e -> showStatistics());
        studentStatsBtn.addActionListener(e -> showStudentStatistics());
        saveBtn.addActionListener(e -> saveBinary());
        loadBtn.addActionListener(e -> loadBinary());
        viewStudentsBtn.addActionListener(e -> showStudentsList());
        viewAdminsBtn.addActionListener(e -> showAdminsList());
        addAdminBtn.addActionListener(e -> showAddAdminDialog());
        logoutBtn.addActionListener(e -> {
            dispose();
            new AdminLoginFrame(library).setVisible(true);
        });

        setLocationRelativeTo(null);
    }
    
    private void saveToXML() {
        try {
            library.saveAllDataToXML();
            JOptionPane.showMessageDialog(this, 
                "All data saved to universite.xml successfully!",
                "Save Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Save failed: " + ex.getMessage(),
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFromXML() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                "This will reload all data from universite.xml.\n" +
                "Current unsaved data will be lost.\n" +
                "Continue?",
                "Confirm Reload",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                library.loadAllDataFromXML();
                loadMediaData();
                JOptionPane.showMessageDialog(this, 
                    "Data reloaded from universite.xml successfully!",
                    "Reload Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Reload failed: " + ex.getMessage(),
                "Reload Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddStudentDialog() {
        NewStudentFrame newStudentFrame = new NewStudentFrame(library);
        newStudentFrame.setVisible(true);
    }
    
    private void showManageSubjectsDialog() {
        JDialog dialog = new JDialog(this, "Manage Subjects", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> subjectList = new JList<>(listModel);
        
        // Récupérer tous les sujets disponibles
        for (Subject subject : library.getAllSubjects()) {
            listModel.addElement(subject.getCode() + " - " + subject.getName());
        }
        
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton closeBtn = new JButton("Close");
        
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(closeBtn);
        
        addBtn.addActionListener(e -> {
            String newSubjectCode = JOptionPane.showInputDialog(dialog, "Enter subject code (e.g., NFA032):");
            if (newSubjectCode != null && !newSubjectCode.trim().isEmpty()) {
                String newSubjectName = JOptionPane.showInputDialog(dialog, "Enter subject name:");
                if (newSubjectName != null && !newSubjectName.trim().isEmpty()) {
                    // Créer un nouveau sujet
                    Specialty defaultSpec = library.getSpecialty("Informatique");
                    if (defaultSpec == null) {
                        defaultSpec = new Specialty("Informatique");
                        library.addSpecialty(defaultSpec);
                    }
                    
                    Subject newSubject = new Subject(newSubjectCode.trim(), newSubjectName.trim(), defaultSpec);
                    library.addSubject(newSubject);
                    defaultSpec.addSubject(newSubject);
                    
                    listModel.addElement(newSubject.getCode() + " - " + newSubject.getName());
                    
                    // Sauvegarder dans XML
                    try {
                        library.saveAllDataToXML();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to save to XML: " + ex.getMessage());
                    }
                }
            }
        });
        
        removeBtn.addActionListener(e -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selected = listModel.get(selectedIndex);
                String code = selected.split(" - ")[0];
                
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Remove subject: " + code + "?\nNote: This won't remove it from enrolled students.",
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove from library
                    Subject subject = library.getSubject(code);
                    if (subject != null) {
                        // Remove from specialty first
                        Specialty spec = subject.getSpecialty();
                        if (spec != null) {
                            spec.getSubjects().remove(subject);
                        }
                        // Then remove from library
                        library.getAllSubjects().remove(subject);
                    }
                    
                    listModel.remove(selectedIndex);
                    
                    // Sauvegarder dans XML
                    try {
                        library.saveAllDataToXML();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to save to XML: " + ex.getMessage());
                    }
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(new JScrollPane(subjectList), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showAddAdminDialog() {
        JDialog dialog = new JDialog(this, "Add Administrator", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prenom:"));
        form.add(prenomField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Confirm Password:"));
        form.add(confirmPasswordField);
        
        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        saveBtn.addActionListener(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier si l'email existe déjà
            if (library.getAllAdministrators().stream().anyMatch(a -> a.getUsername().equals(email)) ||
                library.getAllStudents().stream().anyMatch(s -> s.getUsername().equals(email))) {
                JOptionPane.showMessageDialog(dialog, "Email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Administrator admin = new Administrator(email, password, nom, prenom, email);
            library.addAdministrator(admin);
            
            try {
                library.saveAllDataToXML();
                JOptionPane.showMessageDialog(dialog, 
                    "Administrator added and saved to universite.xml successfully!",
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Administrator added but XML save failed: " + ex.getMessage(),
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showAdminsList() {
        JDialog dialog = new JDialog(this, "Administrator List", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        
        String[] columns = {"Username", "Nom", "Prenom", "Email"};
        DefaultTableModel adminTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable adminTable = new JTable(adminTableModel);
        JScrollPane scrollPane = new JScrollPane(adminTable);
        
        // Populate table
        for (Administrator admin : library.getAllAdministrators()) {
            adminTableModel.addRow(new Object[]{
                admin.getUsername(),
                admin.getNom(),
                admin.getPrenom(),
                admin.getEmail()
            });
        }
        
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");
        
        refreshBtn.addActionListener(e -> {
            adminTableModel.setRowCount(0);
            for (Administrator admin : library.getAllAdministrators()) {
                adminTableModel.addRow(new Object[]{
                    admin.getUsername(),
                    admin.getNom(),
                    admin.getPrenom(),
                    admin.getEmail()
                });
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showStudentStatistics() {
        JDialog dialog = new JDialog(this, "Student Statistics", true);
        dialog.setSize(600, 400);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(statsArea);
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STUDENT STATISTICS ===\n\n");
        
        List<Student> students = library.getAllStudents();
        stats.append("Total Students: ").append(students.size()).append("\n\n");
        
        // Group by specialty
        Map<String, Integer> specialtyCount = new HashMap<>();
        for (Student student : students) {
            String specialty = student.getSpecialty().getName();
            specialtyCount.put(specialty, specialtyCount.getOrDefault(specialty, 0) + 1);
        }
        
        stats.append("Students by Specialty:\n");
        for (Map.Entry<String, Integer> entry : specialtyCount.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        stats.append("\nRecent Students (last 10):\n");
        int count = Math.min(10, students.size());
        for (int i = 0; i < count; i++) {
            Student s = students.get(i);
            stats.append(String.format("  %s %s (%s) - %s\n", 
                s.getNom(), s.getPrenom(), s.getUsername(), s.getSpecialty().getName()));
        }
        
        statsArea.setText(stats.toString());
        dialog.add(scroll);
        dialog.setVisible(true);
    }
    
    private void showStudentsList() {
        JDialog dialog = new JDialog(this, "Student List", true);
        dialog.setSize(700, 500);
        dialog.setLayout(new BorderLayout());
        
        String[] columns = {"Username", "Nom", "Prenom", "Specialty", "Subjects"};
        DefaultTableModel studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable studentTable = new JTable(studentTableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        // Populate table
        for (Student student : library.getAllStudents()) {
            StringBuilder subjects = new StringBuilder();
            for (Subject subject : student.getEnrolledSubjects()) {
                subjects.append(subject.getCode()).append(", ");
            }
            if (subjects.length() > 0) {
                subjects.setLength(subjects.length() - 2);
            }
            
            studentTableModel.addRow(new Object[]{
                student.getUsername(),
                student.getNom(),
                student.getPrenom(),
                student.getSpecialty().getName(),
                subjects.toString()
            });
        }
        
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");
        
        refreshBtn.addActionListener(e -> {
            studentTableModel.setRowCount(0);
            for (Student student : library.getAllStudents()) {
                StringBuilder subjects = new StringBuilder();
                for (Subject subject : student.getEnrolledSubjects()) {
                    subjects.append(subject.getCode()).append(", ");
                }
                if (subjects.length() > 0) {
                    subjects.setLength(subjects.length() - 2);
                }
                
                studentTableModel.addRow(new Object[]{
                    student.getUsername(),
                    student.getNom(),
                    student.getPrenom(),
                    student.getSpecialty().getName(),
                    subjects.toString()
                });
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void showAddMediaDialog() {
        JDialog dialog = new JDialog(this, "Add Media", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(10, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);

        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "document", "video", "quiz" });
        JTextField param1Field = new JTextField();
        JTextField param2Field = new JTextField();
        JLabel param2Label = new JLabel("Param 2 (difficulty - quiz only):");

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Description:"));
        dialog.add(descScroll);
        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Param 1 (pages/duration):"));
        dialog.add(param1Field);
        dialog.add(param2Label);
        dialog.add(param2Field);

        // Update param2 label based on type selection
        typeCombo.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            if ("quiz".equals(type)) {
                param2Label.setText("Param 2 (difficulty - quiz only):");
                param2Field.setEnabled(true);
            } else {
                param2Label.setText("Param 2 (optional):");
                param2Field.setEnabled(false);
                param2Field.setText("");
            }
        });

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        dialog.add(saveBtn);
        dialog.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                MediaFactory factory = MediaFactoryRegistry.getInstance().getFactory(type);

                Object[] params;
                if ("quiz".equals(type)) {
                    params = new Object[] { Integer.parseInt(param1Field.getText()), param2Field.getText() };
                } else {
                    params = new Object[] { Integer.parseInt(param1Field.getText()) };
                }

                Media media = factory.createMedia(
                        idField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        Integer.parseInt(yearField.getText()),
                        descArea.getText(),
                        params);

                // Add to subjects
                if (!library.getAllSubjects().isEmpty()) {
                    // Add to first few subjects
                    List<Subject> subjects = library.getAllSubjects();
                    int count = Math.min(3, subjects.size());
                    for (int i = 0; i < count; i++) {
                        media.addSubject(subjects.get(i));
                    }
                } else {
                    // Create default subjects if none exist
                    Specialty info = new Specialty("Informatique");
                    library.addSpecialty(info);
                    Subject subject = new Subject("NFA032", "Programming Basics", info);
                    library.addSubject(subject);
                    info.addSubject(subject);
                    media.addSubject(subject);
                }

                library.addMedia(media);
                loadMediaData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Media added successfully");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelectedMedia() {
        int row = mediaTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete media " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                library.removeMedia(id);
                loadMediaData();
            }
        }
    }

    private void exportMedia(String format) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export to " + format);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Exporter exporter;
                if ("XML".equals(format)) {
                    exporter = new XMLExporter();
                } else {
                    exporter = new CSVExporter();
                }
                exporter.export(library.getAllMedia(), chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Export completed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        }
    }

    private void showStatistics() {
        JDialog dialog = new JDialog(this, "Statistics", true);
        dialog.setSize(500, 400);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(statsArea);
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== MEDIA STATISTICS ===\n\n");
        stats.append("Total media: ").append(library.getAllMedia().size()).append("\n");

        stats.append("\nTop 5 most accessed:\n");
        List<Media> top = library.getMostAccessedMedia(5);
        for (int i = 0; i < top.size(); i++) {
            Media m = top.get(i);
            stats.append(String.format("%d. %s (%s) - %d accesses%n",
                    i + 1, m.getTitle(), m.getId(), m.getAccessCount()));
        }
        
        // Media by type
        Map<String, Integer> typeCount = new HashMap<>();
        for (Media media : library.getAllMedia()) {
            String type = media.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        
        stats.append("\nMedia by type:\n");
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        statsArea.setText(stats.toString());
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private void saveBinary() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Binary Data");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                library.saveToBinary(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Data saved successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
            }
        }
    }

    private void loadBinary() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Binary Data");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                library.loadFromBinary(chooser.getSelectedFile().getAbsolutePath());
                loadMediaData();
                JOptionPane.showMessageDialog(this, "Data loaded successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
            }
        }
    }

    private void loadMediaData() {
        tableModel.setRowCount(0);
        for (Media media : library.getAllMedia()) {
            tableModel.addRow(new Object[] {
                    media.getId(),
                    media.getTitle(),
                    media.getAuthor(),
                    media.getPublicationYear(),
                    media.getType(),
                    media.getAccessCount()
            });
        }
    }
}

// ==================== MAIN CLASS ====================

public class MediaLibraryApp {
    public static void main(String[] args) {
        // Initialize library
        MediaLibrary library = new MediaLibrary();

        try {
            // Load all data from universite.xml
            library.loadAllDataFromXML();
            System.out.println("Data loaded from universite.xml");
            
            // Create sample media if none exist
            if (library.getAllMedia().isEmpty()) {
                createSampleMedia(library);
            }
            
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
            
            // Create default data if loading fails
            createDefaultData(library);
        }

        // Start GUI
        SwingUtilities.invokeLater(() -> {
            String[] options = { "Student", "Administrator" };
            int choice = JOptionPane.showOptionDialog(null,
                    "Welcome to Media Library\nSelect login type:",
                    "Media Library",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                new StudentLoginFrame(library).setVisible(true);
            } else if (choice == 1) {
                new AdminLoginFrame(library).setVisible(true);
            }
        });
    }
    
    private static void createDefaultData(MediaLibrary library) {
        try {
            // Create default administrator
            Administrator defaultAdmin = new Administrator("admin", "admin", "Admin", "System", "admin@isae.edu.lb");
            library.addAdministrator(defaultAdmin);
            
            // Create default specialties
            Specialty info = new Specialty("Informatique");
            Specialty maths = new Specialty("Mathematiques");
            Specialty physics = new Specialty("Physique");
            
            library.addSpecialty(info);
            library.addSpecialty(maths);
            library.addSpecialty(physics);
            
            // Create default subjects
            Subject[] infoSubjects = {
                new Subject("NFA032", "Programming Basics", info),
                new Subject("NFA035", "Advanced Programming", info),
                new Subject("NFP121", "Database Systems", info)
            };
            
            for (Subject s : infoSubjects) {
                library.addSubject(s);
                info.addSubject(s);
            }
            
            // Create sample media
            createSampleMedia(library);
            
            // Save to XML
            library.saveAllDataToXML();
            System.out.println("Default data created and saved to universite.xml");
            
        } catch (Exception e) {
            System.out.println("Error creating default data: " + e.getMessage());
        }
    }
    
    private static void createSampleMedia(MediaLibrary library) {
        try {
            MediaFactoryRegistry registry = MediaFactoryRegistry.getInstance();

            Media doc1 = registry.getFactory("document").createMedia(
                    "DOC001", "Java Programming", "John Doe", 2023,
                    "Introduction to Java programming language",
                    350);

            Media video1 = registry.getFactory("video").createMedia(
                    "VID001", "Data Structures", "Jane Smith", 2022,
                    "Complete course on data structures",
                    120);

            Media quiz1 = registry.getFactory("quiz").createMedia(
                    "QUIZ001", "OOP Quiz", "Dr. Brown", 2023,
                    "Test your object-oriented programming knowledge",
                    30, "Intermediate");
                    
            Media doc2 = registry.getFactory("document").createMedia(
                    "DOC002", "Algorithms", "Robert Johnson", 2021,
                    "Introduction to algorithms and complexity",
                    280);
                    
            Media video2 = registry.getFactory("video").createMedia(
                    "VID002", "Database Design", "Maria Garcia", 2023,
                    "Fundamentals of database design and SQL",
                    90);

            // Add subjects to media
            Specialty info = library.getSpecialty("Informatique");
            if (info != null && !info.getSubjects().isEmpty()) {
                for (Subject subject : info.getSubjects()) {
                    doc1.addSubject(subject);
                    video1.addSubject(subject);
                    quiz1.addSubject(subject);
                    doc2.addSubject(subject);
                    video2.addSubject(subject);
                }
            }

            library.addMedia(doc1);
            library.addMedia(video1);
            library.addMedia(quiz1);
            library.addMedia(doc2);
            library.addMedia(video2);
            
        } catch (Exception e) {
            System.out.println("Error creating sample media: " + e.getMessage());
        }
    }
}