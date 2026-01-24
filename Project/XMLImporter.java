// XMLImporter.java - Version corrigée
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

class XMLDataImporter {
    public static void importData(String filePath, MediaLibrary library) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));

        NodeList specialties = doc.getElementsByTagName("specialite");

        for (int i = 0; i < specialties.getLength(); i++) {
            Element specElem = (Element) specialties.item(i);
            String specName = specElem.getAttribute("nom");

            // Créer ou récupérer la spécialité
            Specialty specialty = library.getSpecialty(specName);
            if (specialty == null) {
                specialty = new Specialty(specName);
                library.addSpecialty(specialty);
            }

            NodeList students = specElem.getElementsByTagName("etudiant");
            for (int j = 0; j < students.getLength(); j++) {
                Element etudElem = (Element) students.item(j);
                String username = etudElem.getAttribute("username");
                String password = etudElem.getAttribute("password");

                // Vérifier si l'étudiant existe déjà
                boolean studentExists = library.getAllStudents().stream()
                    .anyMatch(s -> s.getUsername().equals(username));

                if (!studentExists) {
                    // Créer l'étudiant avec nom et prénom déduits
                    String[] nameParts = username.split("@")[0].split("\\.");
                    String prenom = nameParts.length > 0 ? capitalize(nameParts[0]) : "unknown";
                    String nom = nameParts.length > 1 ? capitalize(nameParts[1]) : "unknown";

                    Student student = new Student(username, password, nom, prenom, specialty);

                    // Ajouter les sujets
                    NodeList values = etudElem.getElementsByTagName("valeur");
                    for (int k = 0; k < values.getLength(); k++) {
                        String subjectCode = values.item(k).getTextContent().trim();

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
}