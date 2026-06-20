package com.isae.medialibrary.view;

import com.isae.medialibrary.model.Administrator;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.util.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class AdminLoginFrame extends JFrame {
    private static final Logger logger = LogUtil.getLogger(AdminLoginFrame.class);
    private MediaLibrary library;
    private JTextField userField;
    private JPasswordField passField;

    public AdminLoginFrame(MediaLibrary library) {
        this.library = library;
        setTitle("Admin Login");
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
        JLabel createAdminLabel = new JLabel("<html><u>Create New Administrator</u></html>");
        createAdminLabel.setForeground(Color.BLUE);
        createAdminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkPanel.add(createAdminLabel);
        mainPanel.add(linkPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill both fields.");
                return;
            }
            Administrator admin = library.authenticateAdministrator(username, password);
            if (admin != null) {
                logger.info("Admin logged in: " + admin.getUsername());
                dispose();
                new AdminMainFrame(library).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
                logger.warning("Failed login attempt for user: " + username);
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        createAdminLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new NewAdminFrame(library).setVisible(true);
            }
        });
    }
}