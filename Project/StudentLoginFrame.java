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

class StudentLoginFrame extends JFrame {
    private MediaLibrary library;
    private JTextField userField;
    private JPasswordField passField;

    public StudentLoginFrame(MediaLibrary lib) {
        this.library = lib;
        setTitle("Student Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
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
        
        // Create account link panel at bottom
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel createAccountLabel = new JLabel("Create account");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add underline effect using HTML
        createAccountLabel.setText("<html><u>Create account</u></html>");
        
        linkPanel.add(createAccountLabel);
        mainPanel.add(linkPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // Action listeners
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            Student student = library.authenticateStudent(username, password);
            if (student != null) {
                dispose();
                new StudentMainFrame(library, student).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        // Create account link action
        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NewStudentFrame newStudentFrame = new NewStudentFrame(library);
                newStudentFrame.setVisible(true);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createAccountLabel.setForeground(new Color(0, 100, 255));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createAccountLabel.setForeground(Color.BLUE);
            }
        });

        setLocationRelativeTo(null);
    }
}