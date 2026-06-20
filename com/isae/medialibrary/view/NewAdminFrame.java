package com.isae.medialibrary.view;

import com.isae.medialibrary.model.Administrator;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.service.PasswordUtil;
import com.isae.medialibrary.util.LogUtil;
import java.util.logging.Logger;

import javax.swing.*;
import java.awt.*;

public class NewAdminFrame extends JFrame {
    private static final Logger logger = LogUtil.getLogger(NewAdminFrame.class);
    private MediaLibrary library;
    private JTextField nomField = new JTextField(15);
    private JTextField prenomField = new JTextField(15);
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JPasswordField confirmPasswordField = new JPasswordField(15);

    public NewAdminFrame(MediaLibrary library) {
        this.library = library;
        setTitle("New Administrator");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveAdministrator());
        cancelButton.addActionListener(e -> dispose());

        nomField.addActionListener(e -> generateUsername());
        prenomField.addActionListener(e -> generateUsername());
    }

    private void generateUsername() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            usernameField.setText((prenom.charAt(0) + nom).toLowerCase());
        }
    }

    private void saveAdministrator() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (library.authenticateAdministrator(username, password) != null ||
            library.getAllStudents().stream().anyMatch(s -> s.getUsername().equals(username))) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hash = PasswordUtil.hashPassword(password);
        Administrator admin = new Administrator(username, hash, nom, prenom);
        library.addAdministrator(admin);

        JOptionPane.showMessageDialog(this, "Administrator added successfully!\nUsername: " + username,
                "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}