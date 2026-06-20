package com.isae.medialibrary.persistence;

import com.isae.medialibrary.model.*;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.service.PasswordUtil;
import com.isae.medialibrary.util.LogUtil;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

public class XmlManager {
    private static final java.util.logging.Logger logger = LogUtil.getLogger(XmlManager.class);
    private static final String XML_FILE = "universite.xml";

    public static void saveAllData(MediaLibrary library) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("issae");
        doc.appendChild(root);

        // Specialties and students
        for (Specialty spec : library.getAllSpecialties()) {
            Element specElem = doc.createElement("specialite");
            specElem.setAttribute("nom", spec.getNom());

            for (Student student : library.getAllStudents()) {
                if (student.getSpecialty().equals(spec)) {
                    Element studElem = doc.createElement("etudiant");
                    studElem.setAttribute("username", student.getUsername());
                    studElem.setAttribute("password", student.getPassword());

                    for (String code : student.getSubjectCodes()) {
                        Element valElem = doc.createElement("valeur");
                        valElem.setTextContent(code);
                        studElem.appendChild(valElem);
                    }
                    specElem.appendChild(studElem);
                }
            }
            root.appendChild(specElem);
        }

        // Administrators
        Element adminsElem = doc.createElement("administrateurs");
        for (Administrator admin : library.getAllAdministrators()) {
            Element adminElem = doc.createElement("administrateur");
            adminElem.setAttribute("username", admin.getUsername());
            adminElem.setAttribute("password", admin.getPassword());
            adminElem.setAttribute("nom", admin.getNom());
            adminElem.setAttribute("prenom", admin.getPrenom());
            adminsElem.appendChild(adminElem);
        }
        root.appendChild(adminsElem);

        // Media
        Element mediaElem = doc.createElement("mediatheque");
        for (Media media : library.getAllMedia()) {
            Element mElem = doc.createElement("media");
            mElem.setAttribute("id", media.getId());
            mElem.setAttribute("type", media.getType());
            mElem.setAttribute("title", media.getTitle());
            mElem.setAttribute("author", media.getAuthor());
            mElem.setAttribute("year", String.valueOf(media.getPublicationYear()));
            mElem.setAttribute("description", media.getDescription());
            mElem.setAttribute("accessCount", String.valueOf(media.getAccessCount()));

            for (String code : media.getSubjectCodes()) {
                Element subjElem = doc.createElement("subject");
                subjElem.setTextContent(code);
                mElem.appendChild(subjElem);
            }

            if (media instanceof DocumentMedia) {
                mElem.setAttribute("pageCount", String.valueOf(((DocumentMedia) media).getPageCount()));
            } else if (media instanceof VideoSession) {
                mElem.setAttribute("duration", String.valueOf(((VideoSession) media).getDurationMinutes()));
            } else if (media instanceof OnlineQuiz) {
                mElem.setAttribute("duration", String.valueOf(((OnlineQuiz) media).getEstimatedDuration()));
                mElem.setAttribute("difficulty", ((OnlineQuiz) media).getDifficultyLevel());
            }
            mediaElem.appendChild(mElem);
        }
        root.appendChild(mediaElem);

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
        logger.info("Data saved to " + XML_FILE);
    }

    public static void loadAllData(MediaLibrary library) throws Exception {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.info("XML file not found. Creating default data.");
            createDefaultData(library);
            saveAllData(library);
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        library.clear();

        Map<String, Subject> subjectMap = new HashMap<>();

        // Build subjects from all student subject codes
        Set<String> subjectCodes = new HashSet<>();
        NodeList specialties = root.getElementsByTagName("specialite");
        for (int i = 0; i < specialties.getLength(); i++) {
            Element specElem = (Element) specialties.item(i);
            NodeList students = specElem.getElementsByTagName("etudiant");
            for (int j = 0; j < students.getLength(); j++) {
                Element studElem = (Element) students.item(j);
                NodeList valeurs = studElem.getElementsByTagName("valeur");
                for (int k = 0; k < valeurs.getLength(); k++) {
                    subjectCodes.add(valeurs.item(k).getTextContent().trim());
                }
            }
        }
        for (String code : subjectCodes) {
            Subject subject = new Subject(code, code, null);
            library.addSubject(subject);
            subjectMap.put(code, subject);
        }

        // Load students
        for (int i = 0; i < specialties.getLength(); i++) {
            Element specElem = (Element) specialties.item(i);
            String specName = specElem.getAttribute("nom");
            Specialty librarySpec = library.getSpecialty(specName);
            if (librarySpec == null) {
                librarySpec = new Specialty(specName);
                library.addSpecialty(librarySpec);
            }

            NodeList students = specElem.getElementsByTagName("etudiant");
            for (int j = 0; j < students.getLength(); j++) {
                Element studElem = (Element) students.item(j);
                String username = studElem.getAttribute("username");
                String password = studElem.getAttribute("password");
                // if password not hashed (if it's plain), hash it now
                if (!password.matches("[a-f0-9]{64}")) { // SHA-256 hex length
                    password = PasswordUtil.hashPassword(password);
                }
                String[] nameParts = username.split("@")[0].split("\\.");
                String prenom = nameParts.length > 0 ? capitalize(nameParts[0]) : "Unknown";
                String nom = nameParts.length > 1 ? capitalize(nameParts[1]) : "Unknown";

                Student student = new Student(username, password, nom, prenom, librarySpec);
                NodeList valeurs = studElem.getElementsByTagName("valeur");
                for (int k = 0; k < valeurs.getLength(); k++) {
                    String code = valeurs.item(k).getTextContent().trim();
                    Subject subject = subjectMap.get(code);
                    if (subject != null) {
                        student.getSubjectCodes().add(code);
                        student.getEnrolledSubjects().add(subject);
                        if (subject.getSpecialty() == null) {
                            subject.setSpecialty(librarySpec);
                        }
                    }
                }
                library.addStudent(student);
            }
        }

        // Load administrators
        NodeList adminNodes = root.getElementsByTagName("administrateur");
        for (int i = 0; i < adminNodes.getLength(); i++) {
            Element adminElem = (Element) adminNodes.item(i);
            String username = adminElem.getAttribute("username");
            String password = adminElem.getAttribute("password");
            if (!password.matches("[a-f0-9]{64}")) {
                password = PasswordUtil.hashPassword(password);
            }
            String nom = adminElem.getAttribute("nom");
            String prenom = adminElem.getAttribute("prenom");
            Administrator admin = new Administrator(username, password, nom, prenom);
            library.addAdministrator(admin);
        }

        // Load media
        NodeList mediaNodes = root.getElementsByTagName("media");
        for (int i = 0; i < mediaNodes.getLength(); i++) {
            Element mElem = (Element) mediaNodes.item(i);
            String id = mElem.getAttribute("id");
            String type = mElem.getAttribute("type");
            String title = mElem.getAttribute("title");
            String author = mElem.getAttribute("author");
            int year = Integer.parseInt(mElem.getAttribute("year"));
            String description = mElem.getAttribute("description");
            int accessCount = Integer.parseInt(mElem.getAttribute("accessCount"));

            Media media = null;
            if ("Document".equals(type)) {
                int pages = Integer.parseInt(mElem.getAttribute("pageCount"));
                media = new DocumentMedia(id, title, author, year, description, pages);
            } else if ("Video Session".equals(type)) {
                int duration = Integer.parseInt(mElem.getAttribute("duration"));
                media = new VideoSession(id, title, author, year, description, duration);
            } else if ("Online Quiz".equals(type)) {
                int duration = Integer.parseInt(mElem.getAttribute("duration"));
                String difficulty = mElem.getAttribute("difficulty");
                media = new OnlineQuiz(id, title, author, year, description, duration, difficulty);
            }

            if (media != null) {
                NodeList subjNodes = mElem.getElementsByTagName("subject");
                for (int j = 0; j < subjNodes.getLength(); j++) {
                    String code = subjNodes.item(j).getTextContent().trim();
                    Subject subject = subjectMap.get(code);
                    if (subject != null) {
                        media.addSubject(subject);
                    }
                }
                // restore access count
                for (int j = 0; j < accessCount; j++) {
                    media.incrementAccessCount();
                }
                library.addMedia(media);
            }
        }
        logger.info("Data loaded from " + XML_FILE);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private static void createDefaultData(MediaLibrary library) {
        Specialty info = new Specialty("Informatique");
        Specialty math = new Specialty("Mathematiques");
        library.addSpecialty(info);
        library.addSpecialty(math);

        Subject nfp121 = new Subject("NFP121", "Programmation Avancée", info);
        Subject nfa035 = new Subject("NFA035", "Structures de Données", info);
        Subject nfa032 = new Subject("NFA032", "Circuits Électroniques", math);
        library.addSubject(nfp121);
        library.addSubject(nfa035);
        library.addSubject(nfa032);

        Student s1 = new Student("etudiant1", PasswordUtil.hashPassword("pass123"), "Dupont", "Jean", info);
        s1.getSubjectCodes().add("NFP121");
        s1.getSubjectCodes().add("NFA035");
        s1.setEnrolledSubjects(List.of(nfp121, nfa035));
        library.addStudent(s1);

        Student s2 = new Student("etudiant2", PasswordUtil.hashPassword("pass456"), "Martin", "Marie", info);
        s2.getSubjectCodes().add("NFP121");
        s2.setEnrolledSubjects(List.of(nfp121));
        library.addStudent(s2);

        Administrator admin = new Administrator("admin", PasswordUtil.hashPassword("admin123"), "Admin", "System");
        library.addAdministrator(admin);

        DocumentMedia doc = new DocumentMedia("DOC001", "Concepts de Généricité", "Prof. Smith", 2024,
                "Document sur les génériques en Java", 45);
        doc.addSubject(nfp121);
        library.addMedia(doc);

        VideoSession video = new VideoSession("VID001", "Introduction aux Patterns", "Dr. Johnson", 2023,
                "Vidéo d'introduction aux patrons de conception", 90);
        video.addSubject(nfp121);
        library.addMedia(video);
    }
}