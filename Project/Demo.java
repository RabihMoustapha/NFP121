import javax.swing.*;

// ==================== MAIN CLASS ====================

public class Demo {
    public static void main(String[] args) {
        // Initialize library
        MediaLibrary library = new MediaLibrary();

        try {
            // Load all data from universite.xml
            library.loadAllDataFromXML();
            System.out.println("Data loaded from universite.xml");
            
            // Create sample media if none exist
            if (library.getAllMedia().isEmpty()) {
                createSampleMedia(library);
            }
            
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
            
            // Create default data if loading fails
            createDefaultData(library);
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
    
    private static void createDefaultData(MediaLibrary library) {
        try {
            // Create default administrator
            Administrator defaultAdmin = new Administrator("admin", "admin", "Admin", "System", "admin@isae.edu.lb");
            library.addAdministrator(defaultAdmin);
            
            // Create default specialties
            Specialty info = new Specialty("Informatique");
            Specialty maths = new Specialty("Mathematiques");
            Specialty physics = new Specialty("Physique");
            
            library.addSpecialty(info);
            library.addSpecialty(maths);
            library.addSpecialty(physics);
            
            // Create default subjects
            Subject[] infoSubjects = {
                new Subject("NFA032", "Programming Basics", info),
                new Subject("NFA035", "Advanced Programming", info),
                new Subject("NFP121", "Database Systems", info)
            };
            
            for (Subject s : infoSubjects) {
                library.addSubject(s);
                info.addSubject(s);
            }
            
            // Create sample media
            createSampleMedia(library);
            
            // Save to XML
            library.saveAllDataToXML();
            System.out.println("Default data created and saved to universite.xml");
            
        } catch (Exception e) {
            System.out.println("Error creating default data: " + e.getMessage());
        }
    }
    
    private static void createSampleMedia(MediaLibrary library) {
        try {
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
            }

            library.addMedia(doc1);
            library.addMedia(video1);
            library.addMedia(quiz1);
            library.addMedia(doc2);
            library.addMedia(video2);
            
        } catch (Exception e) {
            System.out.println("Error creating sample media: " + e.getMessage());
        }
    }
}