import javax.swing.*;
import java.awt.*;


class NewStudentFrame extends JFrame {
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JComboBox<Specialty> specialiteCombo = new JComboBox<>();
    private JList<Subject> subjectsList = new JList<>();
    private DefaultListModel<Subject> subjectsModel = new DefaultListModel<>();
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private MediaLibrary library;
    private boolean usernameManuallyEdited = false;

    public NewStudentFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Student");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialiser les spécialités disponibles
        updateSpecialiteCombo();
        
        // Panel principal avec padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nom
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        // Prénom
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(prenomField, gbc);

        // Spécialité
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Spécialité:"), gbc);
        gbc.gridx = 1;
        formPanel.add(specialiteCombo, gbc);

        // Sujets disponibles
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Sujets (Ctrl+click pour multiple):"), gbc);
        gbc.gridx = 1;
        subjectsList.setModel(subjectsModel);
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectsList.setVisibleRowCount(3);
        formPanel.add(new JScrollPane(subjectsList), gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> dispose());

        // Auto-générer le username quand nom ou prénom changent
        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
        
        // Marquer le username comme édité manuellement
        usernameField.addActionListener(e -> usernameManuallyEdited = true);

        // Mettre à jour les sujets quand la spécialité change
        specialiteCombo.addActionListener(e -> updateSubjectsList());
        
        // Générer le username initial
        generateUsername();
    }

    private void updateSpecialiteCombo() {
        specialiteCombo.removeAllItems();
        for (Specialty specialty : library.getAllSpecialties()) {
            specialiteCombo.addItem(specialty);
        }
        
        if (library.getAllSpecialties().size() > 0) {
            specialiteCombo.setSelectedIndex(0);
            updateSubjectsList();
        }
    }

    private void updateSubjectsList() {
        subjectsModel.clear();
        Specialty selectedSpecialty = (Specialty) specialiteCombo.getSelectedItem();
        if (selectedSpecialty != null) {
            for (Subject subject : library.getAllSubjects()) {
                if (subject.getSpecialty().equals(selectedSpecialty)) {
                    subjectsModel.addElement(subject);
                }
            }
        }
    }

    private void generateUsername() {
        if (!usernameManuallyEdited) {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            
            if (!nom.isEmpty() && !prenom.isEmpty()) {
                String username = (prenom.charAt(0) + nom).toLowerCase();
                // Nettoyer le username (enlever accents, espaces, etc.)
                username = username.replaceAll("[^a-zA-Z0-9]", "");
                usernameField.setText(username);
            }
        }
    }

    private void saveStudent() {
        try {
            // Validation des champs
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            
            if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez remplir tous les champs obligatoires.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier si le username existe déjà
            if (library.authenticateStudent(username, password) != null || 
                library.authenticateAdministrator(username, password) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Ce nom d'utilisateur existe déjà.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Specialty selectedSpecialty = (Specialty) specialiteCombo.getSelectedItem();
            if (selectedSpecialty == null) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner une spécialité.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            java.util.List<Subject> selectedSubjects = subjectsList.getSelectedValuesList();
            if (selectedSubjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner au moins un sujet.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer l'étudiant
            Student student = new Student(username, password, nom, prenom, selectedSpecialty);
            
            // Inscrire l'étudiant aux sujets sélectionnés
            for (Subject subject : selectedSubjects) {
                student.enrollInSubject(subject);
            }
            
            // Ajouter l'étudiant à la bibliothèque
            library.addStudent(student);
            
            // Sauvegarder dans XML
            try {
                library.saveAllDataToXML("universite.xml");
                JOptionPane.showMessageDialog(this,
                    "Étudiant créé avec succès!\n\n" +
                    "Username: " + username + "\n" +
                    "Spécialité: " + selectedSpecialty.getName(),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Étudiant créé mais erreur lors de la sauvegarde: " + ex.getMessage(),
                    "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
                dispose();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création de l'étudiant: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}