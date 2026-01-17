import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentXMLExporter {

    public static void exportStudents(MediaLibrary library, String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc;
        Element root;

        File file = new File(filePath);

        // Si le fichier existe, on le lit et on l'utilise comme base
        if (file.exists()) {
            doc = builder.parse(file);
            root = doc.getDocumentElement();

            // Vérifier que la racine est bien "issae"
            if (!root.getNodeName().equals("issae")) {
                throw new Exception("Format de fichier XML invalide");
            }
        } else {
            // Si le fichier n'existe pas, créer un nouveau document
            doc = builder.newDocument();
            root = doc.createElement("issae");
            doc.appendChild(root);
        }

        // Pour chaque étudiant de la bibliothèque
        for (Student student : library.getAllStudents()) {
            // Vérifier si l'étudiant existe déjà dans le XML
            if (!studentExistsInXML(doc, student.getUsername())) {
                // Récupérer ou créer la spécialité
                Element specialiteElem = getOrCreateSpecialiteElement(doc, root, student.getSpecialty());

                // Créer l'élément étudiant
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

    private static boolean studentExistsInXML(Document doc, String username) {
        NodeList etudiants = doc.getElementsByTagName("etudiant");
        for (int i = 0; i < etudiants.getLength(); i++) {
            Element etudiant = (Element) etudiants.item(i);
            if (etudiant.getAttribute("username").equals(username)) {
                return true;
            }
        }
        return false;
    }

    private static Element getOrCreateSpecialiteElement(Document doc, Element root, Specialty specialty) {
        // Chercher si la spécialité existe déjà
        NodeList specialites = root.getElementsByTagName("specialite");
        for (int i = 0; i < specialites.getLength(); i++) {
            Element specialite = (Element) specialites.item(i);
            if (specialite.getAttribute("nom").equals(specialty.getName())) {
                return specialite;
            }
        }

        // Si elle n'existe pas, la créer
        Element specialiteElem = doc.createElement("specialite");
        specialiteElem.setAttribute("nom", specialty.getName());
        root.appendChild(specialiteElem);
        return specialiteElem;
    }

    public static void appendStudentToXML(Student student, String filePath) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            Element root;

            File file = new File(filePath);

            System.out.println("Chemin du fichier: " + file.getAbsolutePath());
            System.out.println("Fichier existe: " + file.exists());

            // Si le fichier existe, on le lit et on l'utilise comme base
            if (file.exists()) {
                try {
                    doc = builder.parse(file);
                    System.out.println("Fichier XML parsé avec succès");
                } catch (Exception e) {
                    System.err.println("Erreur lors du parsing du fichier XML: " + e.getMessage());

                    // Tentative de récupération: créer un nouveau document
                    System.out.println("Création d'un nouveau document XML...");
                    doc = builder.newDocument();
                    root = doc.createElement("issae");
                    doc.appendChild(root);

                    JOptionPane.showMessageDialog(null,
                            "Le fichier XML existant était corrompu. Un nouveau fichier a été créé.",
                            "Avertissement",
                            JOptionPane.WARNING_MESSAGE);
                }

                if (doc == null) {
                    doc = builder.newDocument();
                    root = doc.createElement("issae");
                    doc.appendChild(root);
                } else {
                    root = doc.getDocumentElement();

                    // Vérifier que la racine est bien "issae"
                    if (root == null || !root.getNodeName().equals("issae")) {
                        System.err.println("Format invalide. Création d'un nouveau document.");
                        doc = builder.newDocument();
                        root = doc.createElement("issae");
                        doc.appendChild(root);
                    }
                }
            } else {
                // Si le fichier n'existe pas, créer un nouveau document
                System.out.println("Création d'un nouveau fichier XML");
                doc = builder.newDocument();
                root = doc.createElement("issae");
                doc.appendChild(root);
            }

            // Vérifier si l'étudiant existe déjà
            if (studentExistsInXML(doc, student.getUsername())) {
                throw new Exception("Un étudiant avec le nom d'utilisateur '" + student.getUsername() +
                        "' existe déjà dans le fichier XML");
            }

            // Récupérer ou créer la spécialité
            Element specialiteElem = getOrCreateSpecialiteElement(doc, root, student.getSpecialty());

            // Créer l'élément étudiant
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

            System.out.println("Étudiant ajouté au XML avec succès");

        } catch (ParserConfigurationException e) {
            throw new Exception("Erreur de configuration du parser XML: " + e.getMessage(), e);
        } catch (TransformerException e) {
            throw new Exception("Erreur lors de la transformation XML: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception("Erreur d'accès au fichier '" + filePath + "': " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Erreur inattendue lors de la sauvegarde XML: " + e.getMessage(), e);
        }
    }

    // Nouvelle méthode pour mettre à jour un étudiant existant
    public static void updateStudentInXML(Student student, String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc;

        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("Le fichier XML n'existe pas");
        }

        doc = builder.parse(file);
        Element root = doc.getDocumentElement();

        // Chercher l'étudiant à mettre à jour
        NodeList etudiants = doc.getElementsByTagName("etudiant");
        Element studentToUpdate = null;
        Element parentSpecialite = null;

        for (int i = 0; i < etudiants.getLength(); i++) {
            Element etudiant = (Element) etudiants.item(i);
            if (etudiant.getAttribute("username").equals(student.getUsername())) {
                studentToUpdate = etudiant;
                parentSpecialite = (Element) etudiant.getParentNode();
                break;
            }
        }

        if (studentToUpdate == null) {
            throw new Exception("Étudiant non trouvé dans le fichier XML");
        }

        // Mettre à jour le mot de passe
        studentToUpdate.setAttribute("password", student.getPassword());

        // Supprimer les anciennes valeurs et ajouter les nouvelles
        NodeList valeurs = studentToUpdate.getElementsByTagName("valeur");
        List<Element> valeursToRemove = new ArrayList<>();
        for (int i = 0; i < valeurs.getLength(); i++) {
            valeursToRemove.add((Element) valeurs.item(i));
        }
        for (Element valeur : valeursToRemove) {
            studentToUpdate.removeChild(valeur);
        }

        // Ajouter les nouvelles valeurs
        for (Subject subject : student.getEnrolledSubjects()) {
            Element valeurElem = doc.createElement("valeur");
            valeurElem.appendChild(doc.createTextNode(subject.getCode()));
            studentToUpdate.appendChild(valeurElem);
        }

        // Vérifier si la spécialité a changé
        if (!parentSpecialite.getAttribute("nom").equals(student.getSpecialty().getName())) {
            // Supprimer de l'ancienne spécialité
            parentSpecialite.removeChild(studentToUpdate);

            // Ajouter à la nouvelle spécialité
            Element newSpecialiteElem = getOrCreateSpecialiteElement(doc, root, student.getSpecialty());
            newSpecialiteElem.appendChild(studentToUpdate);

            // Supprimer l'ancienne spécialité si elle est vide
            if (parentSpecialite.getElementsByTagName("etudiant").getLength() == 0) {
                root.removeChild(parentSpecialite);
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
}