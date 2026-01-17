import java.awt.*;
import javax.swing.*;


class NewAdminFrame extends JFrame {
    private MediaLibrary library;
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JPasswordField confirmPasswordField = new JPasswordField(15);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    
    public NewAdminFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Administrator");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prenom:"));
        form.add(prenomField);
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Confirm Password:"));
        form.add(confirmPasswordField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Action listeners
        saveButton.addActionListener(e -> saveAdministrator());
        cancelButton.addActionListener(e -> this.dispose());
        
        // Auto-generate username
        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
    }
    
    private void generateUsername() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            String username = (prenom.charAt(0) + nom).toLowerCase();
            usernameField.setText(username);
        }
    }
    
    private void saveAdministrator() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Vérifier si l'username existe déjà
        if (library.authenticateAdministrator(username, password) != null || 
            library.getAllStudents().stream().anyMatch(s -> s.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, 
                "Username already exists", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Créer l'administrateur
        Administrator admin = new Administrator(username, password, nom, prenom);
        library.addAdministrator(admin);
        
        try {
            // Sauvegarder les données
            library.saveAllDataToXML("all_data.xml");
            
            JOptionPane.showMessageDialog(this, 
                "Administrator added successfully!\n\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "Auto-saved to: all_data.xml",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Administrator added but save failed: " + ex.getMessage() + "\n" +
                "Data is only in memory.",
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
        
        this.dispose();
    }
}
