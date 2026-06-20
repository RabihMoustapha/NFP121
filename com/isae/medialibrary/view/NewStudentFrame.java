package com.isae.medialibrary.view;

import com.isae.medialibrary.model.*;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.service.PasswordUtil;
import com.isae.medialibrary.util.LogUtil;
import java.util.logging.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NewStudentFrame extends JFrame {
    private static final Logger logger = LogUtil.getLogger(NewStudentFrame.class);
    private MediaLibrary library;
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JComboBox<Specialty> specialiteCombo = new JComboBox<>();
    private JList<Subject> subjectsList = new JList<>();
    private DefaultListModel<Subject> subjectsModel = new DefaultListModel<>();
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private boolean usernameManuallyEdited = false;

    public NewStudentFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Student");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        updateSpecialiteCombo();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Spécialité:"), gbc);
        gbc.gridx = 1;
        formPanel.add(specialiteCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Sujets (Ctrl+click pour multiple):"), gbc);
        gbc.gridx = 1;
        subjectsList.setModel(subjectsModel);
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectsList.setVisibleRowCount(3);
        formPanel.add(new JScrollPane(subjectsList), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> dispose());

        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
        usernameField.addActionListener(e -> usernameManuallyEdited = true);
        specialiteCombo.addActionListener(e -> updateSubjectsList());
        generateUsername();
    }

    private void updateSpecialiteCombo() {
        specialiteCombo.removeAllItems();
        for (Specialty spec : library.getAllSpecialties()) {
            specialiteCombo.addItem(spec);
        }
        if (library.getAllSpecialties().size() > 0) {
            specialiteCombo.setSelectedIndex(0);
            updateSubjectsList();
        }
    }

    private void updateSubjectsList() {
        subjectsModel.clear();
        Specialty selected = (Specialty) specialiteCombo.getSelectedItem();
        if (selected != null) {
            for (Subject subject : library.getAllSubjects()) {
                if (subject.getSpecialty().equals(selected)) {
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
                String username = (prenom.charAt(0) + nom).toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
                usernameField.setText(username);
            }
        }
    }

    private void saveStudent() {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (library.authenticateStudent(username, password) != null ||
                library.authenticateAdministrator(username, password) != null) {
                JOptionPane.showMessageDialog(this, "Ce nom d'utilisateur existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Specialty selected = (Specialty) specialiteCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une spécialité.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Subject> selectedSubjects = subjectsList.getSelectedValuesList();
            if (selectedSubjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un sujet.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hash = PasswordUtil.hashPassword(password);
            Student student = new Student(username, hash, nom, prenom, selected);
            for (Subject s : selectedSubjects) {
                student.getSubjectCodes().add(s.getCode());
                student.getEnrolledSubjects().add(s);
            }
            library.addStudent(student);

            JOptionPane.showMessageDialog(this,
                    "Étudiant créé avec succès!\nUsername: " + username + "\nSpécialité: " + selected.getNom(),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception ex) {
            logger.error("Error creating student", ex);
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}