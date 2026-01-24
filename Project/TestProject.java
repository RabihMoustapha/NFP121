public class TestProject {
    public static void main(String[] args) {
        System.out.println("=== TEST DU PROJET BIBLIOTHÈQUE MULTIMÉDIA ===\n");
        
        // Test des factories
        MediaFactoryRegistry registry = MediaFactoryRegistry.getInstance();
        
        try {
            // Test création Document
            Media doc = registry.getFactory("document").createMedia(
                "TEST001", "Test Document", "Auteur Test", 2024, 
                "Description test", 50);
            System.out.println("✓ Document créé: " + doc.getType());
            
            // Test création Video
            Media video = registry.getFactory("video").createMedia(
                "TEST002", "Test Video", "Auteur Test", 2024,
                "Description test", 60);
            System.out.println("✓ Vidéo créée: " + video.getType());
            
            // Test création Quiz
            Media quiz = registry.getFactory("quiz").createMedia(
                "TEST003", "Test Quiz", "Auteur Test", 2024,
                "Description test", 30, "Facile");
            System.out.println("✓ Quiz créé: " + quiz.getType());
            
            System.out.println("\n✓ Toutes les factories fonctionnent correctement.");
            
        } catch (Exception e) {
            System.out.println("✗ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}