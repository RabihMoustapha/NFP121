// XMLDataImporter.java
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class XMLDataImporter {
    public static void importData(String filePath, MediaLibrary library) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));

        NodeList specialites = doc.getElementsByTagName("specialite");
        
        for (int i = 0; i < specialites.getLength(); i++) {
            Element specElem = (Element) specialites.item(i);
            String specName = specElem.getAttribute("nom");
            
            // Créer ou récupérer la spécialité
            Specialty specialty = library.getSpecialty(specName);
            if (specialty == null) {
                specialty = new Specialty(specName);
                library.addSpecialty(specialty);
            }

            NodeList etudiants = specElem.getElementsByTagName("etudiant");
            for (int j = 0; j < etudiants.getLength(); j++) {
                Element etudElem = (Element) etudiants.item(j);
                String username = etudElem.getAttribute("username");
                String password = etudElem.getAttribute("password");
                
                // Vérifier si l'étudiant existe déjà
                boolean studentExists = library.getAllStudents().stream()
                    .anyMatch(s -> s.getUsername().equals(username));
                
                if (!studentExists) {
                    // Créer l'étudiant avec nom et prénom déduits
                    String[] nameParts = username.split("@")[0].split("\\.");
                    String prenom = nameParts.length > 0 ? capitalize(nameParts[0]) : "Unknown";
                    String nom = nameParts.length > 1 ? capitalize(nameParts[1]) : "Unknown";
                    
                    Student student = new Student(username, password, nom, prenom, specialty);
                    
                    // Ajouter les sujets
                    NodeList valeurs = etudElem.getElementsByTagName("valeur");
                    for (int k = 0; k < valeurs.getLength(); k++) {
                        String subjectCode = valeurs.item(k).getTextContent().trim();
                        
                        // Créer le sujet s'il n'existe pas
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
    
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    // Méthode pour importer des médias depuis XML (si nécessaire)
    public static List<Media> importMediaFromXML(String filePath, MediaLibrary library) throws Exception {
        List<Media> importedMedia = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));
        
        NodeList mediaNodes = doc.getElementsByTagName("media");
        MediaFactoryRegistry registry = MediaFactoryRegistry.getInstance();
        
        for (int i = 0; i < mediaNodes.getLength(); i++) {
            Element mediaElem = (Element) mediaNodes.item(i);
            String id = mediaElem.getAttribute("id");
            String type = mediaElem.getAttribute("type");
            String title = getElementText(mediaElem, "title");
            String author = getElementText(mediaElem, "author");
            int year = Integer.parseInt(getElementText(mediaElem, "year"));
            String description = getElementText(mediaElem, "description");
            
            MediaFactory factoryObj = registry.getFactory(type);
            Media media;
            
            if ("document".equals(type)) {
                int pages = Integer.parseInt(getElementText(mediaElem, "pages"));
                media = factoryObj.createMedia(id, title, author, year, description, pages);
            } else if ("video".equals(type)) {
                int duration = Integer.parseInt(getElementText(mediaElem, "duration"));
                media = factoryObj.createMedia(id, title, author, year, description, duration);
            } else if ("quiz".equals(type)) {
                int duration = Integer.parseInt(getElementText(mediaElem, "duration"));
                String difficulty = getElementText(mediaElem, "difficulty");
                media = factoryObj.createMedia(id, title, author, year, description, duration, difficulty);
            } else {
                throw new IllegalArgumentException("Unknown media type: " + type);
            }
            
            // Ajouter les sujets
            NodeList subjectNodes = mediaElem.getElementsByTagName("subject");
            for (int j = 0; j < subjectNodes.getLength(); j++) {
                String subjectCode = subjectNodes.item(j).getTextContent();
                Subject subject = library.getSubject(subjectCode);
                if (subject != null) {
                    media.addSubject(subject);
                }
            }
            
            importedMedia.add(media);
        }
        
        return importedMedia;
    }
    
    private static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
}