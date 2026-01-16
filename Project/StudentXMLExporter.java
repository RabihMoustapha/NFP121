import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StudentXMLExporter {
    public static void exportStudents(MediaLibrary library, String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Créer l'élément racine
        Element root = doc.createElement("issae");
        doc.appendChild(root);

        // Organiser les étudiants par spécialité
        Map<Specialty, List<Student>> studentsBySpecialty = new HashMap<>();
        
        for (Student student : library.getAllStudents()) {
            Specialty specialty = student.getSpecialty();
            studentsBySpecialty.computeIfAbsent(specialty, k -> new ArrayList<>()).add(student);
        }

        // Pour chaque spécialité
        for (Map.Entry<Specialty, List<Student>> entry : studentsBySpecialty.entrySet()) {
            Specialty specialty = entry.getKey();
            List<Student> students = entry.getValue();
            
            Element specialiteElem = doc.createElement("specialite");
            specialiteElem.setAttribute("nom", specialty.getName());
            root.appendChild(specialiteElem);

            // Pour chaque étudiant
            for (Student student : students) {
                Element etudiantElem = doc.createElement("etudiant");
                etudiantElem.setAttribute("username", student.getUsername());
                etudiantElem.setAttribute("password", student.getPassword());
                specialiteElem.appendChild(etudiantElem);

                // Ajouter les valeurs (sujets)
                for (Subject subject : student.getEnrolledSubjects()) {
                    Element valeurElem = doc.createElement("valeur");
                    valeurElem.appendChild(doc.createTextNode(subject.getCode()));
                    etudiantElem.appendChild(valeurElem);
                }
            }
        }

        // Écrire dans le fichier
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
    
    public static void exportAllData(MediaLibrary library, String filePath) throws Exception {
        exportStudents(library, filePath);
    }
    
    // Méthode pour exporter des médias en XML (si nécessaire)
    public static void exportMediaToXML(List<Media> mediaList, String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("mediaLibrary");
        doc.appendChild(root);

        for (Media media : mediaList) {
            Element mediaElem = doc.createElement("media");
            mediaElem.setAttribute("id", media.getId());
            mediaElem.setAttribute("type", media.getType().toLowerCase());

            addElement(doc, mediaElem, "title", media.getTitle());
            addElement(doc, mediaElem, "author", media.getAuthor());
            addElement(doc, mediaElem, "year", String.valueOf(media.getPublicationYear()));
            addElement(doc, mediaElem, "description", media.getDescription());
            addElement(doc, mediaElem, "accessCount", String.valueOf(media.getAccessCount()));

            // Ajouter les attributs spécifiques
            if (media instanceof DocumentMedia) {
                addElement(doc, mediaElem, "pages", String.valueOf(((DocumentMedia)media).getPageCount()));
            } else if (media instanceof VideoSession) {
                addElement(doc, mediaElem, "duration", String.valueOf(((VideoSession)media).getDurationMinutes()));
            } else if (media instanceof OnlineQuiz) {
                addElement(doc, mediaElem, "duration", String.valueOf(((OnlineQuiz)media).getEstimatedDuration()));
                addElement(doc, mediaElem, "difficulty", ((OnlineQuiz)media).getDifficultyLevel());
            }

            // Ajouter les sujets
            Element subjectsElem = doc.createElement("subjects");
            for (Subject subject : media.getSubjects()) {
                Element subjectElem = doc.createElement("subject");
                subjectElem.appendChild(doc.createTextNode(subject.getCode()));
                subjectsElem.appendChild(subjectElem);
            }
            mediaElem.appendChild(subjectsElem);
            
            root.appendChild(mediaElem);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
    
    private static void addElement(Document doc, Element parent, String name, String value) {
        Element elem = doc.createElement(name);
        elem.appendChild(doc.createTextNode(value));
        parent.appendChild(elem);
    }
}