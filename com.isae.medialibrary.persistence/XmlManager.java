package com.isae.medialibrary.persistence;

import com.isae.medialibrary.model.*;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.service.PasswordUtil;
import com.isae.medialibrary.util.LogUtil;
import org.slf4j.Logger;

import javax.xml.bind.*;
import java.io.File;
import java.util.*;

public class XmlManager {
    private static final Logger logger = LogUtil.getLogger(XmlManager.class);
    private static final String XML_FILE = "universite.xml";

    public static void saveAllData(MediaLibrary library) throws Exception {
        LibraryData data = new LibraryData();

        // Specialties with their students
        List<Specialty> specialties = library.getAllSpecialties();
        for (Specialty spec : specialties) {
            List<Student> studentsInSpec = new ArrayList<>();
            for (Student s : library.getAllStudents()) {
                if (s.getSpecialty().equals(spec)) {
                    studentsInSpec.add(s);
                }
            }
            spec.setStudents(studentsInSpec);
        }
        data.setSpecialties(specialties);

        data.setAdministrators(library.getAllAdministrators());
        data.setMediaList(library.getAllMedia());

        JAXBContext context = JAXBContext.newInstance(LibraryData.class, Specialty.class, Student.class,
                Administrator.class, DocumentMedia.class, VideoSession.class, OnlineQuiz.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(data, new File(XML_FILE));
        logger.info("Data saved to {}", XML_FILE);
    }

    public static void loadAllData(MediaLibrary library) throws Exception {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.info("XML file not found. Creating default data.");
            createDefaultData(library);
            saveAllData(library);
            return;
        }

        JAXBContext context = JAXBContext.newInstance(LibraryData.class, Specialty.class, Student.class,
                Administrator.class, DocumentMedia.class, VideoSession.class, OnlineQuiz.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        LibraryData data = (LibraryData) unmarshaller.unmarshal(file);

        library.clear();

        Map<String, Subject> subjectMap = new HashMap<>();

        // Build subjects from all student subject codes
        Set<String> subjectCodes = new HashSet<>();
        for (Specialty spec : data.getSpecialties()) {
            for (Student student : spec.getStudents()) {
                subjectCodes.addAll(student.getSubjectCodes());
            }
        }
        for (String code : subjectCodes) {
            Subject subject = new Subject(code, code, null);
            library.addSubject(subject);
            subjectMap.put(code, subject);
        }

        // Load students
        for (Specialty spec : data.getSpecialties()) {
            Specialty librarySpec = library.getSpecialty(spec.getNom());
            for (Student student : spec.getStudents()) {
                String pwd = student.getPassword();
                if (!pwd.startsWith("$2a$")) {
                    student.setPassword(PasswordUtil.hashPassword(pwd));
                }
                student.setSpecialty(librarySpec);
                List<Subject> enrolled = new ArrayList<>();
                for (String code : student.getSubjectCodes()) {
                    Subject subject = subjectMap.get(code);
                    if (subject != null) {
                        enrolled.add(subject);
                        if (subject.getSpecialty() == null) {
                            subject.setSpecialty(librarySpec);
                        }
                    }
                }
                student.setEnrolledSubjects(enrolled);
                library.addStudent(student);
            }
        }

        // Load administrators
        for (Administrator admin : data.getAdministrators()) {
            String pwd = admin.getPassword();
            if (!pwd.startsWith("$2a$")) {
                admin.setPassword(PasswordUtil.hashPassword(pwd));
            }
            library.addAdministrator(admin);
        }

        // Load media
        for (Media media : data.getMediaList()) {
            List<Subject> subjects = new ArrayList<>();
            for (String code : media.getSubjectCodes()) {
                Subject s = subjectMap.get(code);
                if (s != null) subjects.add(s);
            }
            media.setSubjects(subjects);
            library.addMedia(media);
        }

        logger.info("Data loaded from {}", XML_FILE);
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