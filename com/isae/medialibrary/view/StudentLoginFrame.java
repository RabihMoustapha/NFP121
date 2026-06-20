package com.isae.medialibrary.view;

import com.isae.medialibrary.model.Student;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.util.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class StudentLoginFrame extends JFrame {
    private static final Logger logger = LogUtil.getLogger(StudentLoginFrame.class);
    private MediaLibrary library;
    private JTextField userField;
    private JPasswordField passField;

    public StudentLoginFrame(MediaLibrary library) {
        this.library = library;
        setTitle("Student Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        formPanel.add(userField);
        formPanel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        formPanel.add(passField);

        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        formPanel.add(loginBtn);
        formPanel.add(cancelBtn);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel createAccountLabel = new JLabel("<html><u>Create account</u></html>");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkPanel.add(createAccountLabel);
        mainPanel.add(linkPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill both fields.");
                return;
            }
            Student student = library.authenticateStudent(username, password);
            if (student != null) {
                logger.info("Student logged in: " + student.getUsername());
                dispose();
                new StudentMainFrame(library, student).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
                logger.warning("Failed login attempt for user: " + username);
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new NewStudentFrame(library).setVisible(true);
            }
        });
    }
}