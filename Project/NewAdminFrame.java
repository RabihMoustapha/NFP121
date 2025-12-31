import java.awt.*;
import javax.swing.*;


class NewAdminFrame extends JFrame {
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JTextField emailField = new JTextField(50);
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private MediaLibrary library;
    
    public NewAdminFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Student");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prenom:"));
        form.add(prenomField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
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
        saveButton.addActionListener(e -> saveAdmin());
        cancelButton.addActionListener(e -> this.dispose());
        
        // Auto-generate username
        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
    }
    
    private void generateUsername() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            String username = prenom.toLowerCase() + "." + nom.toLowerCase() + "@isae.edu.lb";
            usernameField.setText(username);
        }
    }
    
    private void saveAdmin() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
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
        
        // Créer l'administrateur
        Administrator admin = new Administrator(username, password, nom, prenom, email);
        library.addAdministrator(admin);
        
        try {
            // Sauvegarder automatiquement dans le fichier universite.xml
            library.saveAllDataToXML();
            
            JOptionPane.showMessageDialog(this, 
                "Admin added and saved to universite.xml successfully!\n\n" +
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
