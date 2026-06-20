package com.isae.medialibrary.view;

import com.isae.medialibrary.model.*;
import com.isae.medialibrary.persistence.exporter.CSVExporter;
import com.isae.medialibrary.persistence.exporter.Exporter;
import com.isae.medialibrary.persistence.exporter.XMLExporter;
import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.service.filter.AuthorFilter;
import com.isae.medialibrary.service.filter.TitleFilter;
import com.isae.medialibrary.util.LogUtil;
import org.slf4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AdminMainFrame extends JFrame {
    private static final Logger logger = LogUtil.getLogger(AdminMainFrame.class);
    private MediaLibrary library;
    private JTable mediaTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;

    public AdminMainFrame(MediaLibrary library) {
        this.library = library;
        setTitle("Media Library - Administrator");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton addBtn = new JButton("Add Media");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export");
        JButton statsBtn = new JButton("Stats");
        JButton logoutBtn = new JButton("Logout");
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);
        toolBar.addSeparator();
        toolBar.add(refreshBtn);
        toolBar.add(exportBtn);
        toolBar.add(statsBtn);
        toolBar.addSeparator();
        toolBar.add(logoutBtn);
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Year", "Type", "Accesses", "Subjects"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        mediaTable = new JTable(tableModel);
        mediaTable.getSelectionModel().addListSelectionListener(e -> updateDetails());
        JScrollPane tableScroll = new JScrollPane(mediaTable);
        tableScroll.setPreferredSize(new Dimension(800, 300));
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Details
        detailsArea = new JTextArea(8, 80);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        mainPanel.add(detailsScroll, BorderLayout.SOUTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Title", "Author", "ID"});
        JButton searchBtn = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchType);
        searchPanel.add(searchBtn);
        mainPanel.add(searchPanel, BorderLayout.WEST);

        add(mainPanel);
        loadMediaData();

        // Listeners
        addBtn.addActionListener(e -> {
            AddEditMediaDialog dlg = new AddEditMediaDialog(this, library, null, null);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadMediaData();
        });
        editBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                Media media = library.getMediaWithoutIncrement(id);
                if (media != null) {
                    AddEditMediaDialog dlg = new AddEditMediaDialog(this, library, null, media);
                    dlg.setVisible(true);
                    if (dlg.isSaved()) loadMediaData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a media to edit.");
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = mediaTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                String title = (String) tableModel.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + title + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        library.removeMedia(id);
                        loadMediaData();
                        JOptionPane.showMessageDialog(this, "Media deleted.");
                    } catch (Exception ex) {
                        logger.error("Delete failed", ex);
                        JOptionPane.showMessageDialog(this, "Error deleting media: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a media to delete.");
            }
        });
        refreshBtn.addActionListener(e -> loadMediaData());
        exportBtn.addActionListener(e -> exportData());
        statsBtn.addActionListener(e -> showStatistics());
        logoutBtn.addActionListener(e -> {
            dispose();
            new AdminLoginFrame(library).setVisible(true);
        });
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            String type = (String) searchType.getSelectedItem();
            if (query.isEmpty()) {
                loadMediaData();
                return;
            }
            List<Media> results;
            switch (type) {
                case "Title": results = library.searchByTitle(query); break;
                case "Author": results = library.searchByAuthor(query); break;
                case "ID": {
                    Media m = library.getMediaWithoutIncrement(query);
                    results = (m != null) ? List.of(m) : List.of();
                    break;
                }
                default: results = List.of();
            }
            displayMedia(results);
        });
    }

    private void loadMediaData() {
        displayMedia(library.getAllMedia());
    }

    private void displayMedia(List<Media> list) {
        tableModel.setRowCount(0);
        for (Media m : list) {
            StringBuilder subjects = new StringBuilder();
            for (Subject s : m.getSubjects()) {
                if (subjects.length() > 0) subjects.append(", ");
                subjects.append(s.getCode());
            }
            tableModel.addRow(new Object[]{
                m.getId(), m.getTitle(), m.getAuthor(), m.getPublicationYear(),
                m.getType(), m.getAccessCount(), subjects.toString()
            });
        }
    }

    private void updateDetails() {
        int row = mediaTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            Media m = library.getMediaWithoutIncrement(id);
            if (m != null) {
                detailsArea.setText(
                    "ID: " + m.getId() + "\n" +
                    "Title: " + m.getTitle() + "\n" +
                    "Author: " + m.getAuthor() + "\n" +
                    "Year: " + m.getPublicationYear() + "\n" +
                    "Type: " + m.getType() + "\n" +
                    "Description: " + m.getDescription() + "\n" +
                    "Specific details: " + m.getSpecificDetails() + "\n" +
                    "Accesses: " + m.getAccessCount() + "\n" +
                    "Subjects: " + m.getSubjects()
                );
            }
        }
    }

    private void exportData() {
        String[] formats = {"XML", "CSV"};
        int choice = JOptionPane.showOptionDialog(this, "Choose export format:", "Export",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, formats, formats[0]);
        if (choice < 0) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("export_media." + (choice == 0 ? "xml" : "csv")));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getPath();
            try {
                Exporter exporter = choice == 0 ? new XMLExporter() : new CSVExporter();
                exporter.export(library.getAllMedia(), path);
                JOptionPane.showMessageDialog(this, "Export successful: " + path);
            } catch (Exception ex) {
                logger.error("Export failed", ex);
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        }
    }

    private void showStatistics() {
        List<Media> top = library.getMostAccessedMedia(5);
        StringBuilder sb = new StringBuilder("=== Statistics ===\n");
        sb.append("Total media: ").append(library.getAllMedia().size()).append("\n");
        sb.append("Total students: ").append(library.getAllStudents().size()).append("\n");
        sb.append("Top 5 accessed media:\n");
        for (int i = 0; i < top.size(); i++) {
            Media m = top.get(i);
            sb.append(String.format("%d. %s (%d accesses)\n", i+1, m.getTitle(), m.getAccessCount()));
        }
        JTextArea area = new JTextArea(sb.toString(), 15, 50);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}