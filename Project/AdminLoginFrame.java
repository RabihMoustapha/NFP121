import java.awt.*;
import javax.swing.*;


class AdminLoginFrame extends JFrame {
    private MediaLibrary library;
    private JTextField userField;
    private JPasswordField passField;

    public AdminLoginFrame(MediaLibrary lib) {
        this.library = lib;
        setTitle("Admin Login");
        setSize(300, 250);
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
        
        // Link panel at bottom
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel createAccountLabel = new JLabel("<html><u>Create Admin Account</u></html>");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.setToolTipText("Click to create a new admin account");
        
        linkPanel.add(createAccountLabel);
        mainPanel.add(linkPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // Action listeners
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            Administrator admin = library.authenticateAdministrator(username, password);
            if (admin != null) {
                dispose();
                new AdminMainFrame(library).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials");
            }
        });

        cancelBtn.addActionListener(e -> System.exit(0));

        // Create account action (opens admin creation)
        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NewAdminFrame newAdminFrame = new NewAdminFrame(library);
                newAdminFrame.setVisible(true);
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