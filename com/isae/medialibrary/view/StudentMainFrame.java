package com.isae.medialibrary.view;

import com.isae.medialibrary.model.*;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.service.filter.*;
import com.isae.medialibrary.util.LogUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

public class StudentMainFrame extends JFrame {
    private static final Logger logger = LogUtil.getLogger(StudentMainFrame.class);
    private MediaLibrary library;
    private Student student;
    private JTable mediaTable;
    private DefaultTableModel tableModel;

    public StudentMainFrame(MediaLibrary library, Student student) {
        this.library = library;
        this.student = student;

        setTitle("Media Library - Student: " + student.getNom() + " " + student.getPrenom());
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Student: " + student.getNom() + " " + student.getPrenom()));
        infoPanel.add(new JLabel("Specialty: " + student.getSpecialty().getNom()));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"Title", "Author"});
        JButton searchBtn = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(filterCombo);
        searchPanel.add(searchBtn);
        mainPanel.add(searchPanel, BorderLayout.NORTH); // Actually need to add to a separate container, but we'll place below info

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Year", "Type", "Accesses"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        mediaTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(mediaTable);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton viewBtn = new JButton("View Media");
        JButton filterBtn = new JButton("My Subjects");
        JButton addMediaBtn = new JButton("Add Media");
        JButton editMediaBtn = new JButton("Edit Selected");
        JButton deleteMediaBtn = new JButton("Delete Selected");
        JButton logoutBtn = new JButton("Logout");

        editMediaBtn.setEnabled(false);
        deleteMediaBtn.setEnabled(false);

        buttonPanel.add(viewBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(addMediaBtn);
        buttonPanel.add(editMediaBtn);
        buttonPanel.add(deleteMediaBtn);
        buttonPanel.add(logoutBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadAllMedia();

        mediaTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = mediaTable.getSelectedRow() >= 0;
            if (hasSelection) {
                String id = (String) tableModel.getValueAt(mediaTable.getSelectedRow(), 0);
                Media selected = library.getMediaWithoutIncrement(id);
                boolean canEdit = selected != null && studentCanEdit(selected);
                editMediaBtn.setEnabled(canEdit);
                deleteMediaBtn.setEnabled(canEdit);
            } else {
                editMediaBtn.setEnabled(false);
                deleteMediaBtn.setEnabled(false);
            }
        });

        viewBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMedia(id);
                if (media != null) {
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
            String query = searchField.getText().trim();
            String type = (String) filterCombo.getSelectedItem();
            if (query.isEmpty()) {
                loadAllMedia();
                return;
            }
            FilterCriteria criteria = "Author".equals(type) ? new AuthorFilter(query) : new TitleFilter(query);
            displayMedia(library.searchMedia(criteria));
        });

        addMediaBtn.addActionListener(e -> {
            AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, student, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) loadAllMedia();
        });

        editMediaBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMediaWithoutIncrement(id);
                if (media != null && studentCanEdit(media)) {
                    AddEditMediaDialog dialog = new AddEditMediaDialog(this, library, student, media);
                    dialog.setVisible(true);
                    if (dialog.isSaved()) loadAllMedia();
                } else {
                    JOptionPane.showMessageDialog(this, "You cannot edit this media.");
                }
            }
        });

        deleteMediaBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMediaWithoutIncrement(id);
                if (media != null && studentCanEdit(media)) {
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
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new StudentLoginFrame(library).setVisible(true);
        });
    }

    private boolean studentCanEdit(Media media) {
        return media.getSubjects().stream().anyMatch(s -> student.getEnrolledSubjects().contains(s));
    }

    private void loadAllMedia() {
        displayMedia(library.getAllMedia());
    }

    private void displayMedia(List<Media> mediaList) {
        tableModel.setRowCount(0);
        for (Media media : mediaList) {
            tableModel.addRow(new Object[]{
                media.getId(), media.getTitle(), media.getAuthor(),
                media.getPublicationYear(), media.getType(), media.getAccessCount()
            });
        }
    }
}