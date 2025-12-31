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