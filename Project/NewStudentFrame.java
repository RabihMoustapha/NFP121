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

class NewStudentFrame extends JFrame {
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JComboBox<String> specialiteCombo = new JComboBox<>();
    private JComboBox<String> valeurCombo = new JComboBox<>(new String[]{"NFA032", "NFA035", "NFP121"});
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private MediaLibrary library;
    
    public NewStudentFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Student");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Initialiser les spécialités disponibles
        updateSpecialiteCombo();
        
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prenom:"));
        form.add(prenomField);
        form.add(new JLabel("Specialite:"));
        form.add(specialiteCombo);
        form.add(new JLabel("Valeur:"));
        form.add(valeurCombo);
        form.add(new JLabel("Username (email):"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Action listeners
        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> this.dispose());
        
        // Auto-generate username
        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
    }
    
    private void updateSpecialiteCombo() {
        specialiteCombo.removeAllItems();
        // Récupérer les spécialités de la bibliothèque
        for (Specialty specialty : library.getAllSpecialties()) {
            specialiteCombo.addItem(specialty.getName());
        }
        // Si vide, ajouter des spécialités par défaut
        if (specialiteCombo.getItemCount() == 0) {
            specialiteCombo.addItem("Informatique");
            specialiteCombo.addItem("Mathematiques");
            specialiteCombo.addItem("Physique");
        }
    }
    
    private void generateUsername() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            String username = prenom.toLowerCase() + "." + nom.toLowerCase() + "@isae.edu.lb";
            usernameField.setText(username);
        }
    }
    
    private void saveStudent() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String specialite = (String) specialiteCombo.getSelectedItem();
        String valeur = (String) valeurCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Vérifier si l'username existe déjà (étudiant ou administrateur)
        if (library.authenticateStudent(username, password) != null || 
            library.getAllStudents().stream().anyMatch(s -> s.getUsername().equals(username)) ||
            library.getAllAdministrators().stream().anyMatch(a -> a.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Créer ou récupérer la spécialité
        Specialty specialtyObj = library.getSpecialty(specialite);
        if (specialtyObj == null) {
            specialtyObj = new Specialty(specialite);
            library.addSpecialty(specialtyObj);
        }
        
        // Créer ou récupérer le sujet
        Subject subject = library.getSubject(valeur);
        if (subject == null) {
            subject = new Subject(valeur, valeur + " - " + specialite, specialtyObj);
            library.addSubject(subject);
            specialtyObj.addSubject(subject);
        }
        
        // Créer l'étudiant
        Student student = new Student(username, password, nom, prenom, specialtyObj);
        student.enrollInSubject(subject);
        library.addStudent(student);
        
        try {
            // Sauvegarder automatiquement dans le fichier universite.xml
            library.saveAllDataToXML();
            
            JOptionPane.showMessageDialog(this, 
                "Student added and saved to universite.xml successfully!\n\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "Auto-saved to: universite.xml",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Student added but XML save failed: " + ex.getMessage() + "\n" +
                "Data is only in memory. Please export manually.",
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
        
        this.dispose();
    }
}
