import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

class MediaLibrary implements Serializable {
    private static final long serialVersionUID = 100L;
    
    private Map<String, Media> mediaMap = new HashMap<>();
    private Map<String, Student> studentMap = new HashMap<>();
    private Map<String, Specialty> specialtyMap = new HashMap<>();
    private Map<String, Subject> subjectMap = new HashMap<>();
    private Map<String, Administrator> adminMap = new HashMap<>();
    
    // Système de notification personnalisé
    private Observable observable = new Observable() {};

    // === Méthodes pour les administrateurs ===
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

    // === Méthodes pour les médias ===
    public void addMedia(Media media) {
        mediaMap.put(media.getId(), media);
        
        // Notifier les observateurs (étudiants)
        observable.notifyObservers(media);
        
        // Notifier les étudiants concernés
        for (Student student : getAllStudents()) {
            if (student.isInterestedInMedia(media)) {
                System.out.println("NOTIFICATION: Nouveau média disponible pour " + 
                    student.getUsername() + ": " + media.getTitle());
                // Simulation d'email
                simulateEmailNotification(student, media);
            }
        }
    }

    private void simulateEmailNotification(Student student, Media media) {
        System.out.println("\n=== EMAIL SIMULATION ===");
        System.out.println("À: " + student.getUsername());
        System.out.println("Objet: Nouveau média disponible");
        System.out.println("Cher " + student.getNom() + " " + student.getPrenom() + ",");
        System.out.println("Un nouveau média a été ajouté à la bibliothèque:");
        System.out.println("Titre: " + media.getTitle());
        System.out.println("Auteur: " + media.getAuthor());
        System.out.println("Type: " + media.getType());
        System.out.println("\nCordialement,");
        System.out.println("Bibliothèque Multimédia ISSAE");
        System.out.println("===========================\n");
    }

    public Media getMedia(String id) {
        Media media = mediaMap.get(id);
        if (media != null) {
            media.incrementAccessCount();
        }
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

    public List<Media> searchByAuthor(String author) {
        return searchMedia(new AuthorFilter(author));
    }

    // === Méthodes pour les étudiants ===
    public void addStudent(Student student) {
        studentMap.put(student.getUsername(), student);
        observable.registerObserver(student);
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

    // === Méthodes pour spécialités/sujets ===
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

    // === Sauvegarde/Chargement XML (format spécifié) ===
    public void saveAllDataToXML(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Créer l'élément racine "issae"
        Element root = doc.createElement("issae");
        doc.appendChild(root);

        // 1. Sauvegarder les étudiants par spécialité
        Map<Specialty, List<Student>> studentsBySpecialty = new HashMap<>();
        for (Student student : getAllStudents()) {
            Specialty specialty = student.getSpecialty();
            studentsBySpecialty.computeIfAbsent(specialty, k -> new ArrayList<>()).add(student);
        }

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
                
                for (Subject subject : student.getEnrolledSubjects()) {
                    Element valeurElem = doc.createElement("valeur");
                    valeurElem.appendChild(doc.createTextNode(subject.getCode()));
                    etudiantElem.appendChild(valeurElem);
                }
                
                specialiteElem.appendChild(etudiantElem);
            }
        }

        // 2. Sauvegarder les administrateurs
        Element adminsElem = doc.createElement("administrateurs");
        for (Administrator admin : getAllAdministrators()) {
            Element adminElem = doc.createElement("administrateur");
            adminElem.setAttribute("username", admin.getUsername());
            adminElem.setAttribute("password", admin.getPassword());
            adminElem.setAttribute("nom", admin.getNom());
            adminElem.setAttribute("prenom", admin.getPrenom());
            adminsElem.appendChild(adminElem);
        }
        root.appendChild(adminsElem);

        // 3. Sauvegarder les médias (section optionnelle pour extension future)
        Element mediaElem = doc.createElement("mediatheque");
        for (Media media : getAllMedia()) {
            Element mediaItemElem = doc.createElement("media");
            mediaItemElem.setAttribute("id", media.getId());
            mediaItemElem.setAttribute("type", media.getType());
            mediaItemElem.setAttribute("title", media.getTitle());
            mediaItemElem.setAttribute("author", media.getAuthor());
            mediaItemElem.setAttribute("year", String.valueOf(media.getPublicationYear()));
            mediaItemElem.setAttribute("description", media.getDescription());
            mediaItemElem.setAttribute("accessCount", String.valueOf(media.getAccessCount()));

            for (Subject subject : media.getSubjects()) {
                Element subjectElem = doc.createElement("subject");
                subjectElem.setTextContent(subject.getCode());
                mediaItemElem.appendChild(subjectElem);
            }

            // Détails spécifiques
            if (media instanceof DocumentMedia) {
                mediaItemElem.setAttribute("pageCount", 
                    String.valueOf(((DocumentMedia) media).getPageCount()));
            } else if (media instanceof VideoSession) {
                mediaItemElem.setAttribute("duration", 
                    String.valueOf(((VideoSession) media).getDurationMinutes()));
            } else if (media instanceof OnlineQuiz) {
                mediaItemElem.setAttribute("duration", 
                    String.valueOf(((OnlineQuiz) media).getEstimatedDuration()));
                mediaItemElem.setAttribute("difficulty", 
                    ((OnlineQuiz) media).getDifficultyLevel());
            }

            mediaElem.appendChild(mediaItemElem);
        }
        root.appendChild(mediaElem);

        // Écrire dans le fichier
        saveXMLDocument(doc, filePath);
    }

    public void loadAllDataFromXML(String filePath) throws Exception {
        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            throw new FileNotFoundException("Fichier XML non trouvé: " + filePath);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!root.getNodeName().equals("issae")) {
            throw new Exception("Format XML invalide: racine 'issae' attendue");
        }

        // Nettoyer les données existantes
        mediaMap.clear();
        studentMap.clear();
        specialtyMap.clear();
        subjectMap.clear();
        adminMap.clear();
        observable.clearObservers();

        // 1. Charger les spécialités et étudiants
        NodeList specialiteNodes = root.getElementsByTagName("specialite");
        for (int i = 0; i < specialiteNodes.getLength(); i++) {
            Element specialiteElem = (Element) specialiteNodes.item(i);
            String specialtyName = specialiteElem.getAttribute("nom");

            Specialty specialty = getSpecialty(specialtyName);
            if (specialty == null) {
                specialty = new Specialty(specialtyName);
                addSpecialty(specialty);
            }

            NodeList etudiantNodes = specialiteElem.getElementsByTagName("etudiant");
            for (int j = 0; j < etudiantNodes.getLength(); j++) {
                Element etudiantElem = (Element) etudiantNodes.item(j);
                String username = etudiantElem.getAttribute("username");
                String password = etudiantElem.getAttribute("password");

                // Extraire nom et prénom du username
                String[] nameParts = extractNamesFromUsername(username);
                String nom = nameParts[0];
                String prenom = nameParts[1];

                if (studentMap.containsKey(username)) {
                    continue; // Étudiant déjà chargé
                }

                Student student = new Student(username, password, nom, prenom, specialty);

                // Charger les sujets
                NodeList valeurNodes = etudiantElem.getElementsByTagName("valeur");
                for (int k = 0; k < valeurNodes.getLength(); k++) {
                    String subjectCode = valeurNodes.item(k).getTextContent();
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
        }

        // 2. Charger les administrateurs
        NodeList adminNodes = root.getElementsByTagName("administrateur");
        for (int i = 0; i < adminNodes.getLength(); i++) {
            Element adminElem = (Element) adminNodes.item(i);
            String username = adminElem.getAttribute("username");
            String password = adminElem.getAttribute("password");
            String nom = adminElem.getAttribute("nom");
            String prenom = adminElem.getAttribute("prenom");

            if (!adminMap.containsKey(username)) {
                Administrator admin = new Administrator(username, password, nom, prenom);
                addAdministrator(admin);
            }
        }

        // 3. Charger les médias (si section présente)
        NodeList mediaNodes = root.getElementsByTagName("media");
        if (mediaNodes.getLength() > 0) {
            for (int i = 0; i < mediaNodes.getLength(); i++) {
                Element mediaElem = (Element) mediaNodes.item(i);
                String id = mediaElem.getAttribute("id");
                String type = mediaElem.getAttribute("type");
                String title = mediaElem.getAttribute("title");
                String author = mediaElem.getAttribute("author");
                int year = Integer.parseInt(mediaElem.getAttribute("year"));
                String description = mediaElem.getAttribute("description");

                if (mediaMap.containsKey(id)) {
                    continue; // Média déjà chargé
                }

                Media media = null;
                MediaFactory factoryInstance = MediaFactoryRegistry.getInstance().getFactory(type.toLowerCase().split(" ")[0]);

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
                    // Ajouter les sujets
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
    }

    private String[] extractNamesFromUsername(String username) {
        // Format: prenom.nom@isae.edu.lb
        String emailPart = username.split("@")[0];
        String[] parts = emailPart.split("\\.");
        String prenom = parts.length > 0 ? capitalize(parts[0]) : "Unknown";
        String nom = parts.length > 1 ? capitalize(parts[1]) : "Unknown";
        return new String[]{nom, prenom};
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void saveXMLDocument(Document doc, String filePath) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    // === Statistiques ===
    public List<Media> getMostAccessedMedia(int limit) {
        List<Media> all = new ArrayList<>(mediaMap.values());
        all.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        int endIndex = Math.min(limit, all.size());
        return all.subList(0, endIndex);
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
        int endIndex = Math.min(limit, result.size());
        return result.subList(0, endIndex);
    }

    public List<Media> getMostAccessedBySubject(Subject subject, int limit) {
        List<Media> result = new ArrayList<>();
        for (Media media : mediaMap.values()) {
            if (media.getSubjects().contains(subject)) {
                result.add(media);
            }
        }

        result.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        int endIndex = Math.min(limit, result.size());
        return result.subList(0, endIndex);
    }

    // === Persistance binaire ===
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

            // Nettoyer
            mediaMap.clear();
            studentMap.clear();
            specialtyMap.clear();
            subjectMap.clear();
            adminMap.clear();
            observable.clearObservers();

            // Charger
            for (Media m : data.mediaList)
                mediaMap.put(m.getId(), m);

            for (Student s : data.students) {
                studentMap.put(s.getUsername(), s);
                observable.registerObserver(s);
            }

            for (Specialty sp : data.specialties)
                specialtyMap.put(sp.getName(), sp);

            for (Subject su : data.subjects)
                subjectMap.put(su.getCode(), su);

            for (Administrator admin : data.administrators)
                adminMap.put(admin.getUsername(), admin);
        }
    }

    // Classe helper pour sérialisation
    private static class LibraryData implements Serializable {
        private static final long serialVersionUID = 101L;
        List<Media> mediaList;
        List<Student> students;
        List<Specialty> specialties;
        List<Subject> subjects;
        List<Administrator> administrators;

        public LibraryData(List<Media> ml, List<Student> st, List<Specialty> sp, List<Subject> su, List<Administrator> ad) {
            mediaList = ml;
            students = st;
            specialties = sp;
            subjects = su;
            administrators = ad;
        }
    }
}