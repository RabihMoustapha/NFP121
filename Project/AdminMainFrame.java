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

class AdminMainFrame extends JFrame {
    private MediaLibrary library;
    private JTable mediaTable;
    private DefaultTableModel tableModel;
    private JButton addStudentBtn;

    public AdminMainFrame(MediaLibrary lib) {
        this.library = lib;

        setTitle("Media Library - Admin Panel");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(4, 5, 5, 5));

        JButton addBtn = new JButton("Add Media");
        JButton deleteBtn = new JButton("Delete Media");
        addStudentBtn = new JButton("Add Student");
        JButton manageSubjectsBtn = new JButton("Manage Subjects");
        
        JButton saveXmlBtn = new JButton("Save to XML");
        JButton loadXmlBtn = new JButton("Reload from XML");
        
        JButton exportXmlBtn = new JButton("Export Media XML");
        JButton exportCsvBtn = new JButton("Export Media CSV");
        JButton statsBtn = new JButton("Media Stats");
        JButton studentStatsBtn = new JButton("Student Stats");
        
        JButton saveBtn = new JButton("Save Binary");
        JButton loadBtn = new JButton("Load Binary");
        JButton viewStudentsBtn = new JButton("View Students");
        JButton viewAdminsBtn = new JButton("View Admins");
        JButton addAdminBtn = new JButton("Add Admin");
        JButton logoutBtn = new JButton("Logout");

        // Ajouter les boutons au panel
        controlPanel.add(addBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(addStudentBtn);
        controlPanel.add(manageSubjectsBtn);
        controlPanel.add(saveXmlBtn);
        
        controlPanel.add(loadXmlBtn);
        controlPanel.add(exportXmlBtn);
        controlPanel.add(exportCsvBtn);
        controlPanel.add(statsBtn);
        controlPanel.add(studentStatsBtn);
        
        controlPanel.add(saveBtn);
        controlPanel.add(loadBtn);
        controlPanel.add(viewStudentsBtn);
        controlPanel.add(viewAdminsBtn);
        controlPanel.add(addAdminBtn);
        controlPanel.add(logoutBtn);

        // Media table
        String[] columns = { "ID", "Title", "Author", "Year", "Type", "Accesses" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mediaTable);

        // Layout
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadMediaData();

        // Event listeners
        addBtn.addActionListener(e -> showAddMediaDialog());
        deleteBtn.addActionListener(e -> deleteSelectedMedia());
        addStudentBtn.addActionListener(e -> showAddStudentDialog());
        manageSubjectsBtn.addActionListener(e -> showManageSubjectsDialog());
        saveXmlBtn.addActionListener(e -> saveToXML());
        loadXmlBtn.addActionListener(e -> loadFromXML());
        exportXmlBtn.addActionListener(e -> exportMedia("XML"));
        exportCsvBtn.addActionListener(e -> exportMedia("CSV"));
        statsBtn.addActionListener(e -> showStatistics());
        studentStatsBtn.addActionListener(e -> showStudentStatistics());
        saveBtn.addActionListener(e -> saveBinary());
        loadBtn.addActionListener(e -> loadBinary());
        viewStudentsBtn.addActionListener(e -> showStudentsList());
        viewAdminsBtn.addActionListener(e -> showAdminsList());
        addAdminBtn.addActionListener(e -> showAddAdminDialog());
        logoutBtn.addActionListener(e -> {
            dispose();
            new AdminLoginFrame(library).setVisible(true);
        });

        setLocationRelativeTo(null);
    }
    
    private void saveToXML() {
        try {
            library.saveAllDataToXML();
            JOptionPane.showMessageDialog(this, 
                "All data saved to universite.xml successfully!",
                "Save Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Save failed: " + ex.getMessage(),
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFromXML() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                "This will reload all data from universite.xml.\n" +
                "Current unsaved data will be lost.\n" +
                "Continue?",
                "Confirm Reload",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                library.loadAllDataFromXML();
                loadMediaData();
                JOptionPane.showMessageDialog(this, 
                    "Data reloaded from universite.xml successfully!",
                    "Reload Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Reload failed: " + ex.getMessage(),
                "Reload Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddStudentDialog() {
        NewStudentFrame newStudentFrame = new NewStudentFrame(library);
        newStudentFrame.setVisible(true);
    }
    
    private void showManageSubjectsDialog() {
        JDialog dialog = new JDialog(this, "Manage Subjects", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> subjectList = new JList<>(listModel);
        
        // Récupérer tous les sujets disponibles
        for (Subject subject : library.getAllSubjects()) {
            listModel.addElement(subject.getCode() + " - " + subject.getName());
        }
        
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton closeBtn = new JButton("Close");
        
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(closeBtn);
        
        addBtn.addActionListener(e -> {
            String newSubjectCode = JOptionPane.showInputDialog(dialog, "Enter subject code (e.g., NFA032):");
            if (newSubjectCode != null && !newSubjectCode.trim().isEmpty()) {
                String newSubjectName = JOptionPane.showInputDialog(dialog, "Enter subject name:");
                if (newSubjectName != null && !newSubjectName.trim().isEmpty()) {
                    // Créer un nouveau sujet
                    Specialty defaultSpec = library.getSpecialty("Informatique");
                    if (defaultSpec == null) {
                        defaultSpec = new Specialty("Informatique");
                        library.addSpecialty(defaultSpec);
                    }
                    
                    Subject newSubject = new Subject(newSubjectCode.trim(), newSubjectName.trim(), defaultSpec);
                    library.addSubject(newSubject);
                    defaultSpec.addSubject(newSubject);
                    
                    listModel.addElement(newSubject.getCode() + " - " + newSubject.getName());
                    
                    // Sauvegarder dans XML
                    try {
                        library.saveAllDataToXML();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to save to XML: " + ex.getMessage());
                    }
                }
            }
        });
        
        removeBtn.addActionListener(e -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selected = listModel.get(selectedIndex);
                String code = selected.split(" - ")[0];
                
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Remove subject: " + code + "?\nNote: This won't remove it from enrolled students.",
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove from library
                    Subject subject = library.getSubject(code);
                    if (subject != null) {
                        // Remove from specialty first
                        Specialty spec = subject.getSpecialty();
                        if (spec != null) {
                            spec.getSubjects().remove(subject);
                        }
                        // Then remove from library
                        library.getAllSubjects().remove(subject);
                    }
                    
                    listModel.remove(selectedIndex);
                    
                    // Sauvegarder dans XML
                    try {
                        library.saveAllDataToXML();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to save to XML: " + ex.getMessage());
                    }
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(new JScrollPane(subjectList), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showAddAdminDialog() {
        JDialog dialog = new JDialog(this, "Add Administrator", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Prenom:"));
        form.add(prenomField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Confirm Password:"));
        form.add(confirmPasswordField);
        
        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        saveBtn.addActionListener(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier si l'email existe déjà
            if (library.getAllAdministrators().stream().anyMatch(a -> a.getUsername().equals(email)) ||
                library.getAllStudents().stream().anyMatch(s -> s.getUsername().equals(email))) {
                JOptionPane.showMessageDialog(dialog, "Email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Administrator admin = new Administrator(email, password, nom, prenom, email);
            library.addAdministrator(admin);
            
            try {
                library.saveAllDataToXML();
                JOptionPane.showMessageDialog(dialog, 
                    "Administrator added and saved to universite.xml successfully!",
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Administrator added but XML save failed: " + ex.getMessage(),
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showAdminsList() {
        JDialog dialog = new JDialog(this, "Administrator List", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        
        String[] columns = {"Username", "Nom", "Prenom", "Email"};
        DefaultTableModel adminTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable adminTable = new JTable(adminTableModel);
        JScrollPane scrollPane = new JScrollPane(adminTable);
        
        // Populate table
        for (Administrator admin : library.getAllAdministrators()) {
            adminTableModel.addRow(new Object[]{
                admin.getUsername(),
                admin.getNom(),
                admin.getPrenom(),
                admin.getEmail()
            });
        }
        
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");
        
        refreshBtn.addActionListener(e -> {
            adminTableModel.setRowCount(0);
            for (Administrator admin : library.getAllAdministrators()) {
                adminTableModel.addRow(new Object[]{
                    admin.getUsername(),
                    admin.getNom(),
                    admin.getPrenom(),
                    admin.getEmail()
                });
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showStudentStatistics() {
        JDialog dialog = new JDialog(this, "Student Statistics", true);
        dialog.setSize(600, 400);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(statsArea);
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STUDENT STATISTICS ===\n\n");
        
        List<Student> students = library.getAllStudents();
        stats.append("Total Students: ").append(students.size()).append("\n\n");
        
        // Group by specialty
        Map<String, Integer> specialtyCount = new HashMap<>();
        for (Student student : students) {
            String specialty = student.getSpecialty().getName();
            specialtyCount.put(specialty, specialtyCount.getOrDefault(specialty, 0) + 1);
        }
        
        stats.append("Students by Specialty:\n");
        for (Map.Entry<String, Integer> entry : specialtyCount.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        stats.append("\nRecent Students (last 10):\n");
        int count = Math.min(10, students.size());
        for (int i = 0; i < count; i++) {
            Student s = students.get(i);
            stats.append(String.format("  %s %s (%s) - %s\n", 
                s.getNom(), s.getPrenom(), s.getUsername(), s.getSpecialty().getName()));
        }
        
        statsArea.setText(stats.toString());
        dialog.add(scroll);
        dialog.setVisible(true);
    }
    
    private void showStudentsList() {
        JDialog dialog = new JDialog(this, "Student List", true);
        dialog.setSize(700, 500);
        dialog.setLayout(new BorderLayout());
        
        String[] columns = {"Username", "Nom", "Prenom", "Specialty", "Subjects"};
        DefaultTableModel studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable studentTable = new JTable(studentTableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        
        // Populate table
        for (Student student : library.getAllStudents()) {
            StringBuilder subjects = new StringBuilder();
            for (Subject subject : student.getEnrolledSubjects()) {
                subjects.append(subject.getCode()).append(", ");
            }
            if (subjects.length() > 0) {
                subjects.setLength(subjects.length() - 2);
            }
            
            studentTableModel.addRow(new Object[]{
                student.getUsername(),
                student.getNom(),
                student.getPrenom(),
                student.getSpecialty().getName(),
                subjects.toString()
            });
        }
        
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");
        
        refreshBtn.addActionListener(e -> {
            studentTableModel.setRowCount(0);
            for (Student student : library.getAllStudents()) {
                StringBuilder subjects = new StringBuilder();
                for (Subject subject : student.getEnrolledSubjects()) {
                    subjects.append(subject.getCode()).append(", ");
                }
                if (subjects.length() > 0) {
                    subjects.setLength(subjects.length() - 2);
                }
                
                studentTableModel.addRow(new Object[]{
                    student.getUsername(),
                    student.getNom(),
                    student.getPrenom(),
                    student.getSpecialty().getName(),
                    subjects.toString()
                });
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void showAddMediaDialog() {
        JDialog dialog = new JDialog(this, "Add Media", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(10, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);

        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "document", "video", "quiz" });
        JTextField param1Field = new JTextField();
        JTextField param2Field = new JTextField();
        JLabel param2Label = new JLabel("Param 2 (difficulty - quiz only):");

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Description:"));
        dialog.add(descScroll);
        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Param 1 (pages/duration):"));
        dialog.add(param1Field);
        dialog.add(param2Label);
        dialog.add(param2Field);

        // Update param2 label based on type selection
        typeCombo.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            if ("quiz".equals(type)) {
                param2Label.setText("Param 2 (difficulty - quiz only):");
                param2Field.setEnabled(true);
            } else {
                param2Label.setText("Param 2 (optional):");
                param2Field.setEnabled(false);
                param2Field.setText("");
            }
        });

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        dialog.add(saveBtn);
        dialog.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                MediaFactory factory = MediaFactoryRegistry.getInstance().getFactory(type);

                Object[] params;
                if ("quiz".equals(type)) {
                    params = new Object[] { Integer.parseInt(param1Field.getText()), param2Field.getText() };
                } else {
                    params = new Object[] { Integer.parseInt(param1Field.getText()) };
                }

                Media media = factory.createMedia(
                        idField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        Integer.parseInt(yearField.getText()),
                        descArea.getText(),
                        params);

                // Add to subjects
                if (!library.getAllSubjects().isEmpty()) {
                    // Add to first few subjects
                    List<Subject> subjects = library.getAllSubjects();
                    int count = Math.min(3, subjects.size());
                    for (int i = 0; i < count; i++) {
                        media.addSubject(subjects.get(i));
                    }
                } else {
                    // Create default subjects if none exist
                    Specialty info = new Specialty("Informatique");
                    library.addSpecialty(info);
                    Subject subject = new Subject("NFA032", "Programming Basics", info);
                    library.addSubject(subject);
                    info.addSubject(subject);
                    media.addSubject(subject);
                }

                library.addMedia(media);
                loadMediaData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Media added successfully");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelectedMedia() {
        int row = mediaTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete media " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                library.removeMedia(id);
                loadMediaData();
            }
        }
    }

    private void exportMedia(String format) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export to " + format);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Exporter exporter;
                if ("XML".equals(format)) {
                    exporter = new XMLExporter();
                } else {
                    exporter = new CSVExporter();
                }
                exporter.export(library.getAllMedia(), chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Export completed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        }
    }

    private void showStatistics() {
        JDialog dialog = new JDialog(this, "Statistics", true);
        dialog.setSize(500, 400);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(statsArea);
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== MEDIA STATISTICS ===\n\n");
        stats.append("Total media: ").append(library.getAllMedia().size()).append("\n");

        stats.append("\nTop 5 most accessed:\n");
        List<Media> top = library.getMostAccessedMedia(5);
        for (int i = 0; i < top.size(); i++) {
            Media m = top.get(i);
            stats.append(String.format("%d. %s (%s) - %d accesses%n",
                    i + 1, m.getTitle(), m.getId(), m.getAccessCount()));
        }
        
        // Media by type
        Map<String, Integer> typeCount = new HashMap<>();
        for (Media media : library.getAllMedia()) {
            String type = media.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        
        stats.append("\nMedia by type:\n");
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        statsArea.setText(stats.toString());
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private void saveBinary() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Binary Data");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                library.saveToBinary(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Data saved successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
            }
        }
    }

    private void loadBinary() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Binary Data");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                library.loadFromBinary(chooser.getSelectedFile().getAbsolutePath());
                loadMediaData();
                JOptionPane.showMessageDialog(this, "Data loaded successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
            }
        }
    }

    private void loadMediaData() {
        tableModel.setRowCount(0);
        for (Media media : library.getAllMedia()) {
            tableModel.addRow(new Object[] {
                    media.getId(),
                    media.getTitle(),
                    media.getAuthor(),
                    media.getPublicationYear(),
                    media.getType(),
                    media.getAccessCount()
            });
        }
    }
}