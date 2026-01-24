import java.awt.*;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

class AdminMainFrame extends JFrame {
    private MediaLibrary library;
    private JTable mediaTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;
    private JComboBox<String> reportTypeCombo;
    private JTextField reportParamField;

    public AdminMainFrame(MediaLibrary library) {
        this.library = library;
        setTitle("Bibliothèque Multimédia - Administration");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barre d'outils en haut
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("Ajouter Média");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton refreshButton = new JButton("Rafraîchir");
        JButton exportButton = new JButton("Exporter");
        JButton statsButton = new JButton("Statistiques");
        JButton logoutButton = new JButton("Déconnexion");

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        toolBar.add(exportButton);
        toolBar.add(statsButton);
        toolBar.addSeparator();
        toolBar.add(logoutButton);

        // Table des médias
        String[] columns = {"ID", "Titre", "Auteur", "Année", "Type", "Accès", "Sujets"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediaTable = new JTable(tableModel);
        mediaTable.setRowHeight(25);
        mediaTable.getSelectionModel().addListSelectionListener(e -> updateDetails());
        
        JScrollPane tableScroll = new JScrollPane(mediaTable);
        tableScroll.setPreferredSize(new Dimension(800, 300));

        // Zone de détails
        detailsArea = new JTextArea(8, 80);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchTypeCombo = new JComboBox<>(new String[]{"Titre", "Auteur", "ID"});
        JButton searchButton = new JButton("Rechercher");
        
        searchPanel.add(new JLabel("Recherche:"));
        searchPanel.add(searchField);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);

        // Panel de rapports
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reportTypeCombo = new JComboBox<>(new String[]{"Médias les plus accédés", 
            "Par spécialité", "Par matière"});
        reportParamField = new JTextField(15);
        JButton generateReportButton = new JButton("Générer Rapport");
        
        reportPanel.add(new JLabel("Rapports:"));
        reportPanel.add(reportTypeCombo);
        reportPanel.add(new JLabel("Paramètre:"));
        reportPanel.add(reportParamField);
        reportPanel.add(generateReportButton);

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        centerPanel.add(detailsScroll, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.add(reportPanel, BorderLayout.NORTH);

        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Charger les données
        loadMediaData();

        // Listeners
        addButton.addActionListener(e -> {
            AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, null, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadMediaData();
            }
        });

        editButton.addActionListener(e -> {
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
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un média à modifier.");
            }
        });

        deleteButton.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                String title = (String) tableModel.getValueAt(row, 1);
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Supprimer le média: " + title + "?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    library.removeMedia(id);
                    try {
                        library.saveAllDataToXML("universite.xml");
                        library.saveToBinary("universite.dat");
                        loadMediaData();
                        JOptionPane.showMessageDialog(this, "Média supprimé avec succès.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la suppression: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un média à supprimer.");
            }
        });

        refreshButton.addActionListener(e -> loadMediaData());

        exportButton.addActionListener(e -> exportData());

        statsButton.addActionListener(e -> showStatistics());

        logoutButton.addActionListener(e -> {
            dispose();
            new AdminLoginFrame(library).setVisible(true);
        });

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            String type = (String) searchTypeCombo.getSelectedItem();
            
            if (query.isEmpty()) {
                loadMediaData();
                return;
            }
            
            List<Media> results;
            switch (type) {
                case "Titre":
                    results = library.searchByTitle(query);
                    break;
                case "Auteur":
                    results = library.searchByAuthor(query);
                    break;
                case "ID":
                    Media media = library.getMediaWithoutIncrement(query);
                    results = media != null ? List.of(media) : List.of();
                    break;
                default:
                    results = List.of();
            }
            
            displayMedia(results);
        });

        generateReportButton.addActionListener(e -> generateReport());
    }

    private void loadMediaData() {
        displayMedia(library.getAllMedia());
    }

    private void displayMedia(List<Media> mediaList) {
        tableModel.setRowCount(0);
        for (Media media : mediaList) {
            // Construire la liste des sujets
            StringBuilder subjects = new StringBuilder();
            for (Subject subject : media.getSubjects()) {
                if (subjects.length() > 0) subjects.append(", ");
                subjects.append(subject.getCode());
            }
            
            tableModel.addRow(new Object[]{
                media.getId(),
                media.getTitle(),
                media.getAuthor(),
                media.getPublicationYear(),
                media.getType(),
                media.getAccessCount(),
                subjects.toString()
            });
        }
    }

    private void updateDetails() {
        int row = mediaTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            Media media = library.getMediaWithoutIncrement(id);
            if (media != null) {
                detailsArea.setText("ID: " + media.getId() + "\n" +
                    "Titre: " + media.getTitle() + "\n" +
                    "Auteur: " + media.getAuthor() + "\n" +
                    "Année: " + media.getPublicationYear() + "\n" +
                    "Type: " + media.getType() + "\n" +
                    "Description: " + media.getDescription() + "\n" +
                    "Détails spécifiques: " + media.getSpecificDetails() + "\n" +
                    "Nombre d'accès: " + media.getAccessCount() + "\n" +
                    "Sujets: " + getSubjectsString(media.getSubjects()));
            }
        }
    }

    private String getSubjectsString(java.util.Set<Subject> subjects) {
        StringBuilder sb = new StringBuilder();
        for (Subject subject : subjects) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(subject.getCode()).append(" (").append(subject.getName()).append(")");
        }
        return sb.toString();
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les données");
        
        String[] formats = {"XML", "CSV"};
        int choice = JOptionPane.showOptionDialog(this,
            "Choisissez le format d'export:",
            "Exporter",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            formats,
            formats[0]);
        
        if (choice == 0) { // XML
            fileChooser.setSelectedFile(new File("export_medias.xml"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    XMLExporter exporter = new XMLExporter();
                    exporter.export(library.getAllMedia(), fileChooser.getSelectedFile().getPath());
                    JOptionPane.showMessageDialog(this, 
                        "Export XML réussi vers: " + fileChooser.getSelectedFile().getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'export XML: " + ex.getMessage());
                }
            }
        } else if (choice == 1) { // CSV
            fileChooser.setSelectedFile(new File("export_medias.csv"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    CSVExporter exporter = new CSVExporter();
                    exporter.export(library.getAllMedia(), fileChooser.getSelectedFile().getPath());
                    JOptionPane.showMessageDialog(this, 
                        "Export CSV réussi vers: " + fileChooser.getSelectedFile().getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'export CSV: " + ex.getMessage());
                }
            }
        }
    }

    private void showStatistics() {
        String report = "=== STATISTIQUES DE LA BIBLIOTHÈQUE ===\n\n";
        report += "Nombre total de médias: " + library.getAllMedia().size() + "\n";
        report += "Nombre total d'étudiants: " + library.getAllStudents().size() + "\n";
        report += "Nombre total d'administrateurs: " + library.getAllAdministrators().size() + "\n";
        report += "Spécialités disponibles: " + library.getAllSpecialties().size() + "\n\n";
        
        // Médias les plus accédés
        List<Media> topMedia = library.getMostAccessedMedia(5);
        if (!topMedia.isEmpty()) {
            report += "=== Top 5 des médias les plus accédés ===\n";
            for (int i = 0; i < topMedia.size(); i++) {
                Media m = topMedia.get(i);
                report += String.format("%d. %s (%s) - %d accès\n", 
                    i+1, m.getTitle(), m.getType(), m.getAccessCount());
            }
        }
        
        JTextArea reportArea = new JTextArea(report, 20, 60);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Statistiques", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String param = reportParamField.getText().trim();
        
        if ("Par spécialité".equals(reportType) || "Par matière".equals(reportType)) {
            if (param.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer un paramètre (nom de spécialité ou code de matière).");
                return;
            }
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== RAPPORT ===\n\n");
        
        if ("Médias les plus accédés".equals(reportType)) {
            List<Media> topMedia = library.getMostAccessedMedia(10);
            report.append("Top 10 des médias les plus accédés:\n");
            for (int i = 0; i < topMedia.size(); i++) {
                Media m = topMedia.get(i);
                report.append(String.format("%d. %s (ID: %s) - %d accès\n", 
                    i+1, m.getTitle(), m.getId(), m.getAccessCount()));
            }
        } else if ("Par spécialité".equals(reportType)) {
            Specialty specialty = library.getSpecialty(param);
            if (specialty == null) {
                JOptionPane.showMessageDialog(this, "Spécialité non trouvée: " + param);
                return;
            }
            
            List<Media> topMedia = library.getMostAccessedBySpecialty(specialty, 10);
            report.append("Top 10 des médias les plus accédés pour ").append(param).append(":\n");
            for (int i = 0; i < topMedia.size(); i++) {
                Media m = topMedia.get(i);
                report.append(String.format("%d. %s (ID: %s) - %d accès\n", 
                    i+1, m.getTitle(), m.getId(), m.getAccessCount()));
            }
        } else if ("Par matière".equals(reportType)) {
            Subject subject = library.getSubject(param);
            if (subject == null) {
                JOptionPane.showMessageDialog(this, "Matière non trouvée: " + param);
                return;
            }
            
            List<Media> topMedia = library.getMostAccessedBySubject(subject, 10);
            report.append("Top 10 des médias les plus accédés pour ").append(param).append(":\n");
            for (int i = 0; i < topMedia.size(); i++) {
                Media m = topMedia.get(i);
                report.append(String.format("%d. %s (ID: %s) - %d accès\n", 
                    i+1, m.getTitle(), m.getId(), m.getAccessCount()));
            }
        }
        
        JTextArea reportArea = new JTextArea(report.toString(), 15, 50);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Rapport Généré", JOptionPane.INFORMATION_MESSAGE);
    }
}