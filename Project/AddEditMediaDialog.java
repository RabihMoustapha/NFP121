import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class AddEditMediaDialog extends JDialog {
    private MediaLibrary library;
    private Student student;
    private Media originalMedia;
    private boolean saved = false;
    
    // Composants UI
    private JTextField idField, titleField, authorField, yearField;
    private JTextArea descArea;
    private JComboBox<String> typeCombo;
    private JTextField param1Field, param2Field;
    private JLabel param2Label;
    private JList<Subject> subjectsList;
    private DefaultListModel<Subject> subjectsModel;
    
    public AddEditMediaDialog(Frame parent, MediaLibrary lib, Student stud, Media media) {
        super(parent, media == null ? "Add Media" : "Edit Media", true);
        this.library = lib;
        this.student = stud;
        this.originalMedia = media;
        
        setSize(500, 600);
        setLayout(new BorderLayout());
        
        // Panel principal avec formulaire
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ID (non modifiable en édition)
        formPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        if (media != null) {
            idField.setText(media.getId());
            idField.setEditable(false);
        }
        formPanel.add(idField);
        
        // Autres champs
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField(media != null ? media.getTitle() : "");
        formPanel.add(titleField);
        
        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField(media != null ? media.getAuthor() : "");
        formPanel.add(authorField);
        
        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField(media != null ? String.valueOf(media.getPublicationYear()) : "");
        formPanel.add(yearField);
        
        formPanel.add(new JLabel("Description:"));
        descArea = new JTextArea(3, 20);
        if (media != null) descArea.setText(media.getDescription());
        formPanel.add(new JScrollPane(descArea));
        
        // Type
        formPanel.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"document", "video", "quiz"});
        if (media != null) {
            if (media instanceof DocumentMedia) typeCombo.setSelectedItem("document");
            else if (media instanceof VideoSession) typeCombo.setSelectedItem("video");
            else if (media instanceof OnlineQuiz) typeCombo.setSelectedItem("quiz");
        }
        formPanel.add(typeCombo);
        
        // Paramètres spécifiques
        formPanel.add(new JLabel("Param 1 (pages/duration):"));
        param1Field = new JTextField();
        if (media != null) {
            if (media instanceof DocumentMedia) 
                param1Field.setText(String.valueOf(((DocumentMedia)media).getPageCount()));
            else if (media instanceof VideoSession) 
                param1Field.setText(String.valueOf(((VideoSession)media).getDurationMinutes()));
            else if (media instanceof OnlineQuiz) 
                param1Field.setText(String.valueOf(((OnlineQuiz)media).getEstimatedDuration()));
        }
        formPanel.add(param1Field);
        
        param2Label = new JLabel("Param 2 (difficulty - quiz only):");
        formPanel.add(param2Label);
        param2Field = new JTextField();
        param2Field.setEnabled(false);
        if (media instanceof OnlineQuiz) {
            param2Field.setText(((OnlineQuiz)media).getDifficultyLevel());
            param2Field.setEnabled(true);
        }
        formPanel.add(param2Field);
        
        // Gestion du type pour activer/désactiver param2
        typeCombo.addActionListener(e -> {
            if ("quiz".equals(typeCombo.getSelectedItem())) {
                param2Label.setText("Param 2 (difficulty - quiz only):");
                param2Field.setEnabled(true);
            } else {
                param2Label.setText("Param 2 (optional):");
                param2Field.setEnabled(false);
                param2Field.setText("");
            }
        });
        
        // Liste des sujets
        formPanel.add(new JLabel("Subjects:"));
        subjectsModel = new DefaultListModel<>();
        subjectsList = new JList<>(subjectsModel);
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Charger tous les sujets, pré-sélectionner ceux pertinents
        for (Subject subject : library.getAllSubjects()) {
            subjectsModel.addElement(subject);
        }
        
        if (media != null) {
            // Pour modification: pré-sélectionner les sujets actuels du média
            List<Subject> selected = new ArrayList<>(media.getSubjects());
            int[] indices = new int[selected.size()];
            int index = 0;
            for (int i = 0; i < subjectsModel.size(); i++) {
                if (selected.contains(subjectsModel.get(i))) {
                    indices[index++] = i;
                }
            }
            subjectsList.setSelectedIndices(indices);
        } else if (student != null) {
            // Pour un nouvel ajout par étudiant: pré-sélectionner ses sujets
            List<Subject> studentSubjects = new ArrayList<>(student.getEnrolledSubjects());
            int[] indices = new int[studentSubjects.size()];
            int index = 0;
            for (int i = 0; i < subjectsModel.size(); i++) {
                if (studentSubjects.contains(subjectsModel.get(i))) {
                    indices[index++] = i;
                }
            }
            subjectsList.setSelectedIndices(indices);
        }
        
        formPanel.add(new JScrollPane(subjectsList));
        
        add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> saveMedia());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(parent);
    }
    
    private void saveMedia() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            String desc = descArea.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            
            // Validation des champs obligatoires
            if (id.isEmpty() || title.isEmpty() || author.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields: ID, Title, Author, and Description.",
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier que l'année est valide
            if (year < 1900 || year > 2100) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid year between 1900 and 2100.",
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier unicité ID pour nouvel ajout
            if (originalMedia == null && library.getMediaWithoutIncrement(id) != null) {
                JOptionPane.showMessageDialog(this, 
                    "ID already exists. Please choose a different ID.",
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation des paramètres spécifiques
            if (param1Field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a value for parameter 1 (pages for document, duration for video/quiz).",
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int param1 = Integer.parseInt(param1Field.getText().trim());
            if (param1 <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Parameter 1 must be a positive number.",
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer le média via Factory
            MediaFactory factory = MediaFactoryRegistry.getInstance().getFactory(type);
            Media media;
            
            if ("quiz".equals(type)) {
                if (param2Field.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a difficulty level for quiz.",
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String param2 = param2Field.getText().trim();
                media = factory.createMedia(id, title, author, year, desc, param1, param2);
            } else {
                media = factory.createMedia(id, title, author, year, desc, param1);
            }
            
            // Vérifier qu'au moins un sujet est sélectionné
            List<Subject> selectedSubjects = subjectsList.getSelectedValuesList();
            if (selectedSubjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please select at least one subject for this media.",
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ajouter les sujets sélectionnés
            media.clearSubjects();
            for (Subject subject : selectedSubjects) {
                media.addSubject(subject);
            }
            
            // Sauvegarder
            if (originalMedia == null) {
                library.addMedia(media);
            } else {
                // Mettre à jour dans la bibliothèque
                library.removeMedia(originalMedia.getId());
                library.addMedia(media);
            }
            
            saved = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid number format. Please check year and parameter fields.",
                "Number Format Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(),
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Unexpected error: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}