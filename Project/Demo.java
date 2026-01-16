import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Demo {
    public static void main(String[] args) {
        MediaLibrary library = new MediaLibrary();

        try {
            // Try to load students from XML if file exists
            File xmlFile = new File("university.xml");
            if (xmlFile.exists()) {
                XMLDataImporter.importData("university.xml", library);
                System.out.println("Students loaded from XML: " + "university.xml");
            } else {
                System.out.println("No XML file found, creating sample data...");
                createSampleData(library);
                
                // Save sample data to XML
                try {
                    StudentXMLExporter.exportStudents(library, "university.xml");
                    System.out.println("Sample data saved to: " + "university.xml");
                } catch (Exception e) {
                    System.out.println("Could not save sample data to XML: " + e.getMessage());
                }
            }

            // Create sample media
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
            } else {
                // Create default subjects if none exist
                if (info == null) {
                    info = new Specialty("Informatique");
                    library.addSpecialty(info);
                }
                
                Subject subj1 = new Subject("NFA032", "Programming Basics", info);
                Subject subj2 = new Subject("NFA035", "Advanced Programming", info);
                Subject subj3 = new Subject("NFP121", "Database Systems", info);
                
                library.addSubject(subj1);
                library.addSubject(subj2);
                library.addSubject(subj3);
                info.addSubject(subj1);
                info.addSubject(subj2);
                info.addSubject(subj3);
                
                doc1.addSubject(subj1);
                video1.addSubject(subj2);
                quiz1.addSubject(subj3);
                doc2.addSubject(subj1);
                video2.addSubject(subj3);
            }

            library.addMedia(doc1);
            library.addMedia(video1);
            library.addMedia(quiz1);
            library.addMedia(doc2);
            library.addMedia(video2);

        } catch (Exception e) {
            System.out.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
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
    
    private static void createSampleData(MediaLibrary library) {
        // Create sample specialties
        Specialty info = new Specialty("Informatique");
        Specialty maths = new Specialty("Mathematiques");
        Specialty physics = new Specialty("Physique");
        
        library.addSpecialty(info);
        library.addSpecialty(maths);
        library.addSpecialty(physics);
        
        // Create sample subjects
        Subject[] infoSubjects = {
            new Subject("NFA032", "Programming Basics", info),
            new Subject("NFA035", "Advanced Programming", info),
            new Subject("NFP121", "Database Systems", info)
        };
        
        Subject[] mathsSubjects = {
            new Subject("MATH101", "Calculus I", maths),
            new Subject("MATH102", "Calculus II", maths)
        };
        
        Subject[] physicsSubjects = {
            new Subject("PHYS101", "Mechanics", physics),
            new Subject("PHYS102", "Electromagnetism", physics)
        };
        
        for (Subject s : infoSubjects) {
            library.addSubject(s);
            info.addSubject(s);
        }
        
        for (Subject s : mathsSubjects) {
            library.addSubject(s);
            maths.addSubject(s);
        }
        
        for (Subject s : physicsSubjects) {
            library.addSubject(s);
            physics.addSubject(s);
        }
        
        // Create sample students
        Student[] students = {
            new Student("jdoe", "password123", "Doe", "John", info),
            new Student("jsmith", "password123", "Smith", "Jane", maths),
            new Student("bjones", "password123", "Jones", "Bob", physics),
            new Student("alice", "password123", "Johnson", "Alice", info),
            new Student("charlie", "password123", "Brown", "Charlie", maths)
        };
        
        for (Student s : students) {
            // Enroll in random subjects
            for (Subject subject : s.getSpecialty().getSubjects()) {
                if (Math.random() > 0.5) {
                    s.enrollInSubject(subject);
                }
            }
            library.addStudent(s);
        }
    }

}