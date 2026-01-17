import javax.swing.*;
import java.io.File;

public class MediaLibraryApp {
        private static final String DEFAULT_DATA_FILE = "universite.xml";
    
    public static void main(String[] args) {
        // Initialize library
        MediaLibrary library = new MediaLibrary();

        try {
            // Try to load all data from XML if file exists
            File dataFile = new File(DEFAULT_DATA_FILE);
            if (dataFile.exists()) {
                library.loadAllDataFromXML(DEFAULT_DATA_FILE);
                System.out.println("All data loaded from XML: " + DEFAULT_DATA_FILE);
            } else {
                System.out.println("No data file found, creating sample data...");
                createSampleData(library);
                
                // Create default admin if none exist
                if (library.getAllAdministrators().isEmpty()) {
                    Administrator defaultAdmin = new Administrator("admin", "admin", "Admin", "System");
                    library.addAdministrator(defaultAdmin);
                }
                
                // Save sample data to XML
                try {
                    library.saveAllDataToXML(DEFAULT_DATA_FILE);
                    System.out.println("Sample data saved to: " + DEFAULT_DATA_FILE);
                } catch (Exception e) {
                    System.out.println("Could not save sample data to XML: " + e.getMessage());
                }
            }

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
        
        Administrator defaultAdmin = new Administrator("admin", "admin", "Admin", "System");
        library.addAdministrator(defaultAdmin);
    }
}