import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Set;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class MediaLibrary extends Observable {
    private Map<String, Media> mediaMap = new HashMap<>();
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Specialty> specialtyMap = new HashMap<>();
    private Map<String, Subject> subjectMap = new HashMap<>();
    private Map<String, Administrator> adminMap = new HashMap<>();

    // Méthodes pour les administrateurs
    public void addAdministrator(Administrator admin) {
        adminMap.put(admin.getUsername(), admin);
    }

    public List<Administrator> getAllAdministrators() {
        return new ArrayList<>(adminMap.values());
    }

    public Administrator authenticateAdministrator(String username, String password) {
        Administrator admin = adminMap.get(username);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    // Méthode pour sauvegarder toutes les données en XML
    public void saveAllDataToXML(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("mediaLibraryData");
        doc.appendChild(root);

        // Sauvegarder les étudiants
        Element studentsElem = doc.createElement("students");
        for (Student student : getAllStudents()) {
            Element studentElem = doc.createElement("student");
            studentElem.setAttribute("username", student.getUsername());
            studentElem.setAttribute("password", student.getPassword());
            studentElem.setAttribute("nom", student.getNom());
            studentElem.setAttribute("prenom", student.getPrenom());
            studentElem.setAttribute("specialty", student.getSpecialty().getName());
            
            for (Subject subject : student.getEnrolledSubjects()) {
                Element subjectElem = doc.createElement("enrolledSubject");
                subjectElem.setTextContent(subject.getCode());
                studentElem.appendChild(subjectElem);
            }
            studentsElem.appendChild(studentElem);
        }
        root.appendChild(studentsElem);

        // Sauvegarder les administrateurs
        Element adminsElem = doc.createElement("administrators");
        for (Administrator admin : getAllAdministrators()) {
            Element adminElem = doc.createElement("administrator");
            adminElem.setAttribute("username", admin.getUsername());
            adminElem.setAttribute("password", admin.getPassword());
            adminElem.setAttribute("nom", admin.getNom());
            adminElem.setAttribute("prenom", admin.getPrenom());
            adminsElem.appendChild(adminElem);
        }
        root.appendChild(adminsElem);

        // Sauvegarder les médias
        Element mediaElem = doc.createElement("media");
        for (Media media : getAllMedia()) {
            Element mediaItemElem = doc.createElement("mediaItem");
            mediaItemElem.setAttribute("id", media.getId());
            mediaItemElem.setAttribute("type", media.getType());
            mediaItemElem.setAttribute("title", media.getTitle());
            mediaItemElem.setAttribute("author", media.getAuthor());
            mediaItemElem.setAttribute("year", String.valueOf(media.getPublicationYear()));
            mediaItemElem.setAttribute("accessCount", String.valueOf(media.getAccessCount()));
            mediaItemElem.setAttribute("description", media.getDescription());
            
            // Ajouter les sujets du média
            for (Subject subject : media.getSubjects()) {
                Element subjectElem = doc.createElement("subject");
                subjectElem.setTextContent(subject.getCode());
                mediaItemElem.appendChild(subjectElem);
            }
            
            // Ajouter les détails spécifiques
            if (media instanceof DocumentMedia) {
                mediaItemElem.setAttribute("pageCount", String.valueOf(((DocumentMedia) media).getPageCount()));
            } else if (media instanceof VideoSession) {
                mediaItemElem.setAttribute("duration", String.valueOf(((VideoSession) media).getDurationMinutes()));
            } else if (media instanceof OnlineQuiz) {
                mediaItemElem.setAttribute("duration", String.valueOf(((OnlineQuiz) media).getEstimatedDuration()));
                mediaItemElem.setAttribute("difficulty", ((OnlineQuiz) media).getDifficultyLevel());
            }
            
            mediaElem.appendChild(mediaItemElem);
        }
        root.appendChild(mediaElem);

        // Écrire dans le fichier
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    // Méthode pour charger toutes les données depuis XML
    public void loadAllDataFromXML(String filePath) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new File(filePath));

    // 1. Charger les étudiants (fusion)
    NodeList studentNodes = doc.getElementsByTagName("student");
    for (int i = 0; i < studentNodes.getLength(); i++) {
        Element studentElem = (Element) studentNodes.item(i);
        String username = studentElem.getAttribute("username");
        String password = studentElem.getAttribute("password");
        String nom = studentElem.getAttribute("nom");
        String prenom = studentElem.getAttribute("prenom");
        String specialtyName = studentElem.getAttribute("specialty");

        // Vérifier si l'étudiant existe déjà
        Student existingStudent = studentMap.get(username);
        if (existingStudent != null) {
            // L'étudiant existe déjà, on peut choisir de sauter ou de mettre à jour
            // Ici, on choisit de sauter pour éviter les doublons
            System.out.println("Student " + username + " already exists. Skipping...");
            continue;
        }

        // Créer ou récupérer la spécialité
        Specialty specialty = getSpecialty(specialtyName);
        if (specialty == null) {
            specialty = new Specialty(specialtyName);
            addSpecialty(specialty);
        }

        Student student = new Student(username, password, nom, prenom, specialty);

        // Ajouter les sujets
        NodeList subjectNodes = studentElem.getElementsByTagName("enrolledSubject");
        for (int j = 0; j < subjectNodes.getLength(); j++) {
            String subjectCode = subjectNodes.item(j).getTextContent();
            Subject subject = getSubject(subjectCode);
            if (subject == null) {
                subject = new Subject(subjectCode, subjectCode, specialty);
                addSubject(subject);
                specialty.addSubject(subject);
            }
            student.enrollInSubject(subject);
        }

        addStudent(student);
    }

    // 2. Charger les administrateurs (fusion)
    NodeList adminNodes = doc.getElementsByTagName("administrator");
    for (int i = 0; i < adminNodes.getLength(); i++) {
        Element adminElem = (Element) adminNodes.item(i);
        String username = adminElem.getAttribute("username");
        String password = adminElem.getAttribute("password");
        String nom = adminElem.getAttribute("nom");
        String prenom = adminElem.getAttribute("prenom");

        // Vérifier si l'administrateur existe déjà
        Administrator existingAdmin = adminMap.get(username);
        if (existingAdmin != null) {
            System.out.println("Administrator " + username + " already exists. Skipping...");
            continue;
        }

        Administrator admin = new Administrator(username, password, nom, prenom);
        addAdministrator(admin);
    }

    // 3. Charger les médias (fusion)
    NodeList mediaNodes = doc.getElementsByTagName("mediaItem");
    for (int i = 0; i < mediaNodes.getLength(); i++) {
        Element mediaElem = (Element) mediaNodes.item(i);
        String id = mediaElem.getAttribute("id");
        String type = mediaElem.getAttribute("type");
        String title = mediaElem.getAttribute("title");
        String author = mediaElem.getAttribute("author");
        int year = Integer.parseInt(mediaElem.getAttribute("year"));
        String description = mediaElem.getAttribute("description");

        // Vérifier si le média existe déjà
        Media existingMedia = mediaMap.get(id);
        if (existingMedia != null) {
            System.out.println("Media " + id + " already exists. Skipping...");
            continue;
        }

        Media media = null;
        MediaFactory factoryInstance = MediaFactoryRegistry.getInstance().getFactory(type);

        if ("Document".equals(type)) {
            int pageCount = Integer.parseInt(mediaElem.getAttribute("pageCount"));
            media = factoryInstance.createMedia(id, title, author, year, description, pageCount);
        } else if ("Video Session".equals(type)) {
            int duration = Integer.parseInt(mediaElem.getAttribute("duration"));
            media = factoryInstance.createMedia(id, title, author, year, description, duration);
        } else if ("Online Quiz".equals(type)) {
            int duration = Integer.parseInt(mediaElem.getAttribute("duration"));
            String difficulty = mediaElem.getAttribute("difficulty");
            media = factoryInstance.createMedia(id, title, author, year, description, duration, difficulty);
        }

        if (media != null) {
            // Ajouter les sujets au média
            NodeList subjectNodes = mediaElem.getElementsByTagName("subject");
            for (int j = 0; j < subjectNodes.getLength(); j++) {
                String subjectCode = subjectNodes.item(j).getTextContent();
                Subject subject = getSubject(subjectCode);
                if (subject != null) {
                    media.addSubject(subject);
                }
            }

            // Restaurer le compteur d'accès
            int accessCount = Integer.parseInt(mediaElem.getAttribute("accessCount"));
            for (int j = 0; j < accessCount; j++) {
                media.incrementAccessCount();
            }
            addMedia(media);
            }
        }
    }
    
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

    public Media getMediaWithoutIncrement(String id) {
        return mediaMap.get(id);
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

    // XML operations for students
    public void saveStudentsToXML(String filePath) throws Exception {
        StudentXMLExporter.exportStudents(this, filePath);
    }

    public void loadStudentsFromXML(String filePath) throws Exception {
        XMLDataImporter.importData(filePath, this);
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
                    new ArrayList<>(subjectMap.values()));
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
        }
    }

    // Helper class for serialization
    private static class LibraryData implements Serializable {
        private static final long serialVersionUID = 1L;
        List<Media> mediaList;
        List<Student> students;
        List<Specialty> specialties;
        List<Subject> subjects;

        public LibraryData(List<Media> ml, List<Student> st, List<Specialty> sp, List<Subject> su) {
            mediaList = ml;
            students = st;
            specialties = sp;
            subjects = su;
        }
    }
}