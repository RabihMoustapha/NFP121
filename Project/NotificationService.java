class NotificationService {
    public static void sendEmailNotification(Student student, Media media) {
        // Simulation d'envoi d'email
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("À: " + student.getUsername());
        System.out.println("Objet: Nouveau média disponible");
        System.out.println("Cher " + student.getNom() + " " + student.getPrenom() + ",");
        System.out.println("Un nouveau média a été ajouté à la bibliothèque:");
        System.out.println("Titre: " + media.getTitle());
        System.out.println("Auteur: " + media.getAuthor());
        System.out.println("Type: " + media.getType());
        System.out.println("Description: " + media.getDescription());
        System.out.println("\nCordialement,\nBibliothèque Multimédia ISSAE");
        System.out.println("==========================\n");
    }
}