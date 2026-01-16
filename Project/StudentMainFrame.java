import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

class StudentMainFrame extends JFrame {
    private MediaLibrary library;
    private Student student;
    private JTable mediaTable;
    private DefaultTableModel tableModel;

    public StudentMainFrame(MediaLibrary lib, Student stud) {
        this.library = lib;
        this.student = stud;

        setTitle("Media Library - Student: " + stud.getNom() + " " + stud.getPrenom());
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton addMediaBtn = new JButton("Add Media");
        JButton editMediaBtn = new JButton("Edit Selected");
        JButton deleteMediaBtn = new JButton("Delete Selected");

        // Écouteurs
        addMediaBtn.addActionListener(e -> showAddMediaDialog());
        editMediaBtn.addActionListener(e -> editSelectedMedia());
        deleteMediaBtn.addActionListener(e -> deleteSelectedMedia());

        // Mettre à jour l'état des boutons selon la sélection
        mediaTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = mediaTable.getSelectedRow() >= 0;
            if (hasSelection) {
                String id = (String) tableModel.getValueAt(mediaTable.getSelectedRow(), 0);
                Media selected = library.getMedia(id); // Note: getMedia incrémente l'accès, mais c'est acceptable
                boolean canEdit = student.canEditMedia(selected);
                editMediaBtn.setEnabled(canEdit);
                deleteMediaBtn.setEnabled(canEdit);
            } else {
                editMediaBtn.setEnabled(false);
                deleteMediaBtn.setEnabled(false);
            }
        });

        // Student info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Student: " + stud.getNom() + " " + stud.getPrenom()));
        infoPanel.add(new JLabel("Specialty: " + stud.getSpecialty().getName()));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JComboBox<String> filterCombo = new JComboBox<>(new String[] { "Title", "Author" });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(filterCombo);
        searchPanel.add(searchBtn);

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

        // Details area
        JTextArea detailsArea = new JTextArea(5, 60);
        detailsArea.setEditable(false);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton viewBtn = new JButton("View Media");
        JButton filterBtn = new JButton("My Subjects");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(viewBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(logoutBtn);
        buttonPanel.add(addMediaBtn);
        buttonPanel.add(editMediaBtn);
        buttonPanel.add(deleteMediaBtn);

        // Layout
        setLayout(new BorderLayout(5, 5));
        add(infoPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(detailsScroll, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadAllMedia();

        // Event listeners
        viewBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMedia(id);
                if (media != null) {
                    detailsArea.setText("Title: " + media.getTitle() + "\n" +
                            "Author: " + media.getAuthor() + "\n" +
                            "Description: " + media.getDescription() + "\n" +
                            "Type: " + media.getType() + "\n" +
                            media.getSpecificDetails());
                    JOptionPane.showMessageDialog(this, "Media accessed. Count incremented.");
                }
            }
        });

        filterBtn.addActionListener(e -> {
            FilterComposite filter = new FilterComposite(FilterComposite.Operator.OR);
            for (Subject subject : student.getEnrolledSubjects()) {
                filter.addCriterion(new SubjectFilter(subject));
            }
            displayMedia(library.searchMedia(filter));
        });

        searchBtn.addActionListener(e -> {
            String query = searchField.getText();
            String filterType = (String) filterCombo.getSelectedItem();

            if (query.isEmpty()) {
                loadAllMedia();
                return;
            }

            FilterCriteria criteria;
            if ("Author".equals(filterType)) {
                criteria = new AuthorFilter(query);
            } else {
                criteria = new TitleFilter(query);
            }

            displayMedia(library.searchMedia(criteria));
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new StudentLoginFrame(library).setVisible(true);
        });

        setLocationRelativeTo(null);
    }

    private void loadAllMedia() {
        displayMedia(library.getAllMedia());
    }

    private void displayMedia(List<Media> mediaList) {
        tableModel.setRowCount(0);
        for (Media media : mediaList) {
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

    private void showAddMediaDialog() {
        AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, student, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadAllMedia();
        }
    }

    private void editSelectedMedia() {
        int row = mediaTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            Media media = library.getMedia(id);
            if (media != null && student.canEditMedia(media)) {
                AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, student, media);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    loadAllMedia();
                }
            } else {
                JOptionPane.showMessageDialog(this, "You cannot edit this media.");
            }
        }
    }

    private void deleteSelectedMedia() {
        int row = mediaTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            Media media = library.getMedia(id);
            if (media != null && student.canEditMedia(media)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete media: " + media.getTitle() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    library.removeMedia(id);
                    loadAllMedia();
                }
            } else {
                JOptionPane.showMessageDialog(this, "You cannot delete this media.");
            }
        }
    }
}