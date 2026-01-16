// AdminMainFrame.java
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;

class AdminMainFrame extends JFrame {
    private MediaLibrary library;
    private JTable mediaTable;
    private DefaultTableModel tableModel;

    public AdminMainFrame(MediaLibrary lib) {
        this.library = lib;

        setTitle("Media Library - Admin Panel");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(4, 5, 5, 5));

        JButton addBtn = new JButton("Add Media");
        JButton deleteBtn = new JButton("Delete Media");
        JButton modifyBtn = new JButton("Modify Media");
        JButton addStudentBtn = new JButton("Add Student");
        JButton manageSubjectsBtn = new JButton("Manage Subjects");
        
        JButton exportStudentsXmlBtn = new JButton("Export Students XML");
        JButton importStudentsXmlBtn = new JButton("Import Students XML");
        
        JButton exportXmlBtn = new JButton("Export Media XML");
        JButton exportCsvBtn = new JButton("Export Media CSV");
        JButton statsBtn = new JButton("Media Stats");
        JButton studentStatsBtn = new JButton("Student Stats");
        
        JButton saveBtn = new JButton("Save Binary");
        JButton loadBtn = new JButton("Load Binary");
        JButton viewStudentsBtn = new JButton("View Students");
        JButton backupAllBtn = new JButton("Backup All Data");
        JButton restoreBackupBtn = new JButton("Restore Backup");
        JButton logoutBtn = new JButton("Logout");

        // Ajouter les boutons au panel
        controlPanel.add(addBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(modifyBtn);
        controlPanel.add(addStudentBtn);
        controlPanel.add(manageSubjectsBtn);
        controlPanel.add(exportStudentsXmlBtn);
        
        controlPanel.add(importStudentsXmlBtn);
        controlPanel.add(exportXmlBtn);
        controlPanel.add(exportCsvBtn);
        controlPanel.add(statsBtn);
        controlPanel.add(studentStatsBtn);
        
        controlPanel.add(saveBtn);
        controlPanel.add(loadBtn);
        controlPanel.add(viewStudentsBtn);
        controlPanel.add(backupAllBtn);
        controlPanel.add(restoreBackupBtn);
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
        addBtn.addActionListener(e -> {
            AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, null, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadMediaData();
            }
        });
        
        modifyBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMediaWithoutIncrement(id);
                if (media != null) {
                    AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, null, media);
                    dialog.setVisible(true);
                    if (dialog.isSaved()) {
                        loadMediaData();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a media to modify.");
            }
        });
        
        deleteBtn.addActionListener(e -> deleteSelectedMedia());
        addStudentBtn.addActionListener(e -> showAddStudentDialog());
        manageSubjectsBtn.addActionListener(e -> showManageSubjectsDialog());
        exportStudentsXmlBtn.addActionListener(e -> exportStudentsXML());
        importStudentsXmlBtn.addActionListener(e -> importStudentsXML());
        exportXmlBtn.addActionListener(e -> exportMedia("XML"));
        exportCsvBtn.addActionListener(e -> exportMedia("CSV"));
        statsBtn.addActionListener(e -> showStatistics());
        studentStatsBtn.addActionListener(e -> showStudentStatistics());
        saveBtn.addActionListener(e -> saveBinary());
        loadBtn.addActionListener(e -> loadBinary());
        viewStudentsBtn.addActionListener(e -> showStudentsList());
        backupAllBtn.addActionListener(e -> backupAllData());
        restoreBackupBtn.addActionListener(e -> restoreFromBackup());
        logoutBtn.addActionListener(e -> {
            dispose();
            new AdminLoginFrame(library).setVisible(true);
        });

        setLocationRelativeTo(null);
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
                    listModel.remove(selectedIndex);
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(new JScrollPane(subjectList), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void exportStudentsXML() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Students to XML");
        chooser.setSelectedFile(new File("students_data.xml"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                library.saveStudentsToXML(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Students exported to XML successfully!\n\n" +
                    "File: " + chooser.getSelectedFile().getName(),
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Export failed: " + ex.getMessage(),
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importStudentsXML() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Students from XML");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "This will import students from XML.\n" +
                    "Existing students with same username will be skipped.\n" +
                    "Continue?",
                    "Confirm Import",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    library.loadStudentsFromXML(chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, 
                        "Students imported from XML successfully!",
                        "Import Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Import failed: " + ex.getMessage(),
                    "Import Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void backupAllData() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Backup All Data");
        chooser.setSelectedFile(new File("media_library_backup.xml"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Sauvegarder les étudiants
                StudentXMLExporter.exportStudents(library, chooser.getSelectedFile().getAbsolutePath());
                
                // Sauvegarder les médias dans un fichier séparé
                String mediaFile = chooser.getSelectedFile().getAbsolutePath()
                    .replace(".xml", "_media.xml");
                XMLExporter mediaExporter = new XMLExporter();
                mediaExporter.export(library.getAllMedia(), mediaFile);
                
                JOptionPane.showMessageDialog(this, 
                    "All data backed up successfully!\n\n" +
                    "Students: " + chooser.getSelectedFile().getName() + "\n" +
                    "Media: " + new File(mediaFile).getName(),
                    "Backup Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Backup failed: " + ex.getMessage(),
                    "Backup Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restoreFromBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Restore Data from Backup");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "This will restore data from backup.\n" +
                    "Current data may be overwritten.\n" +
                    "Continue?",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Restaurer les étudiants
                    XMLDataImporter.importData(chooser.getSelectedFile().getAbsolutePath(), library);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Students restored successfully!",
                        "Restore Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Restore failed: " + ex.getMessage(),
                    "Restore Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showStudentStatistics() {
        JDialog dialog = new JDialog(this, "Student Statistics", true);
        dialog.setSize(600, 400);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(statsArea);
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STUDENT STATISTICS ===\n\n");
        
        java.util.List<Student> students = library.getAllStudents();
        stats.append("Total Students: ").append(students.size()).append("\n\n");
        
        // Group by specialty
        java.util.Map<String, Integer> specialtyCount = new java.util.HashMap<>();
        for (Student student : students) {
            String specialty = student.getSpecialty().getName();
            specialtyCount.put(specialty, specialtyCount.getOrDefault(specialty, 0) + 1);
        }
        
        stats.append("Students by Specialty:\n");
        for (java.util.Map.Entry<String, Integer> entry : specialtyCount.entrySet()) {
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
        java.util.List<Media> top = library.getMostAccessedMedia(5);
        for (int i = 0; i < top.size(); i++) {
            Media m = top.get(i);
            stats.append(String.format("%d. %s (%s) - %d accesses%n",
                    i + 1, m.getTitle(), m.getId(), m.getAccessCount()));
        }
        
        // Media by type
        java.util.Map<String, Integer> typeCount = new java.util.HashMap<>();
        for (Media media : library.getAllMedia()) {
            String type = media.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        
        stats.append("\nMedia by type:\n");
        for (java.util.Map.Entry<String, Integer> entry : typeCount.entrySet()) {
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