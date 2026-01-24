import javax.swing.*;
import java.io.File;

public class MediaLibraryApp {
    private static final String DEFAULT_DATA_FILE = "universite.xml";
    private static final String BINARY_DATA_FILE = "universite.dat";

    public static void main(String[] args) {
        // Initialiser library
        MediaLibrary library = new MediaLibrary();

        try {
            // Essayer de charger depuis fichier binaire d'abord
            File binaryFile = new File(BINARY_DATA_FILE);
            if (binaryFile.exists()) {
                library.loadFromBinary(BINARY_DATA_FILE);
                System.out.println("Données chargées depuis le fichier binaire.");
            } else {
                // Sinon essayer XML
                File xmlFile = new File(DEFAULT_DATA_FILE);
                if (xmlFile.exists()) {
                    library.loadAllDataFromXML(DEFAULT_DATA_FILE);
                    System.out.println("Données chargées depuis XML: " + DEFAULT_DATA_FILE);
                } else {
                    System.out.println("Aucun fichier de données trouvé, création de données d'exemple...");
                    createSampleData(library);

                    // Créer admin par défaut si aucun n'existe
                    if (library.getAllAdministrators().isEmpty()) {
                        Administrator defaultAdmin = new Administrator("admin", "admin123", "Admin", "System");
                        library.addAdministrator(defaultAdmin);
                    }

                    // Sauvegarder les données d'exemple
                    try {
                        library.saveAllDataToXML(DEFAULT_DATA_FILE);
                        library.saveToBinary(BINARY_DATA_FILE);
                        System.out.println("Données d'exemple sauvegardées.");
                    } catch (Exception e) {
                        System.out.println("Erreur lors de la sauvegarde: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }

        // Démarrer l'interface graphique
        SwingUtilities.invokeLater(() -> {
            String[] options = { "Étudiant", "Administrateur" };
            int choice = JOptionPane.showOptionDialog(
                null,
                "Choisissez le mode d'accès:",
                "Bibliothèque Multimédia - ISSAE",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice == 0) { // Étudiant
                new StudentLoginFrame(library).setVisible(true);
            } else if (choice == 1) { // Administrateur
                new AdminLoginFrame(library).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }

    private static void createSampleData(MediaLibrary library) {
        try {
            // Créer des spécialités
            Specialty info = new Specialty("Informatique");
            Specialty electronique = new Specialty("Électronique");
            library.addSpecialty(info);
            library.addSpecialty(electronique);

            // Créer des matières
            Subject nfp121 = new Subject("NFP121", "Programmation Avancée", info);
            Subject nfa035 = new Subject("NFA035", "Structures de Données", info);
            Subject nfa032 = new Subject("NFA032", "Circuits Électroniques", electronique);
            
            library.addSubject(nfp121);
            library.addSubject(nfa035);
            library.addSubject(nfa032);
            info.addSubject(nfp121);
            info.addSubject(nfa035);
            electronique.addSubject(nfa032);

            // Créer des étudiants
            Student student1 = new Student("etudiant1", "pass123", "Dupont", "Jean", info);
            student1.enrollInSubject(nfp121);
            student1.enrollInSubject(nfa035);
            library.addStudent(student1);

            Student student2 = new Student("etudiant2", "pass456", "Martin", "Marie", info);
            student2.enrollInSubject(nfp121);
            library.addStudent(student2);

            Student student3 = new Student("etudiant3", "pass789", "Durand", "Pierre", electronique);
            student3.enrollInSubject(nfa032);
            library.addStudent(student3);

            // Créer des médias
            MediaFactoryRegistry registry = MediaFactoryRegistry.getInstance();
            
            // Document
            Media doc1 = registry.getFactory("document").createMedia(
                "DOC001", 
                "Concepts de Généricité", 
                "Prof. Smith", 
                2024, 
                "Document sur les génériques en Java",
                45
            );
            doc1.addSubject(nfp121);
            doc1.addSubject(nfa035);
            library.addMedia(doc1);

            // Video
            Media video1 = registry.getFactory("video").createMedia(
                "VID001",
                "Introduction aux Patterns",
                "Dr. Johnson",
                2023,
                "Vidéo d'introduction aux patrons de conception",
                90
            );
            video1.addSubject(nfp121);
            library.addMedia(video1);

            // Quiz
            Media quiz1 = registry.getFactory("quiz").createMedia(
                "QUIZ001",
                "Quiz Structures de Données",
                "Prof. Wilson",
                2024,
                "Quiz sur les structures de données fondamentales",
                30,
                "Intermédiaire"
            );
            quiz1.addSubject(nfa035);
            library.addMedia(quiz1);

            // Simuler quelques accès
            for (int i = 0; i < 5; i++) doc1.incrementAccessCount();
            for (int i = 0; i < 3; i++) video1.incrementAccessCount();
            for (int i = 0; i < 7; i++) quiz1.incrementAccessCount();

        } catch (Exception e) {
            System.out.println("Erreur lors de la création des données d'exemple: " + e.getMessage());
        }
    }
}