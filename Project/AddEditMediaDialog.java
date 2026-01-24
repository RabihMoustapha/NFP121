// AddEditMediaDialog.java
import javax.swing.*;
import java.awt.*;

class AddEditMediaDialog extends JDialog {
    private MediaLibrary library;
    private Student student;  // Peut être null pour admin
    private Media originalMedia;
    private boolean saved = false;

    // Components UI
    private JTextField idField, titleField, authorField, yearField;
    private JTextArea descArea;
    private JComboBox<String> typeCombo;
    private JTextField param1Field, param2Field;
    private JLabel param1Label, param2Label;
    private JList<Subject> subjectsList;
    private DefaultListModel<Subject> subjectsModel;

    // Constructeur avec 4 paramètres (pour admin et étudiant)
    public AddEditMediaDialog(Frame parent, MediaLibrary lib, Student stud, Media media) {
        super(parent, media == null ? "Ajouter un média" : "Modifier un média", true);
        this.library = lib;
        this.student = stud;  // Peut être null pour admin
        this.originalMedia = media;

        setSize(550, 650);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        // Panel principal avec formulaire
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID*:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        if (media != null) {
            idField.setText(media.getId());
            idField.setEditable(false);
        }
        formPanel.add(idField, gbc);

        // Titre
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Titre*:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        if (media != null) titleField.setText(media.getTitle());
        formPanel.add(titleField, gbc);

        // Auteur
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Auteur*:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField(20);
        if (media != null) authorField.setText(media.getAuthor());
        formPanel.add(authorField, gbc);

        // Année
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Année*:"), gbc);
        gbc.gridx = 1;
        yearField = new JTextField(20);
        if (media != null) yearField.setText(String.valueOf(media.getPublicationYear()));
        formPanel.add(yearField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Type*:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Document", "Video Session", "Online Quiz"});
        if (media != null) {
            typeCombo.setSelectedItem(media.getType());
        }
        typeCombo.addActionListener(e -> updateParamLabels());
        formPanel.add(typeCombo, gbc);

        // Paramètre 1
        gbc.gridx = 0; gbc.gridy = 5;
        param1Label = new JLabel("Pages:");
        formPanel.add(param1Label, gbc);
        gbc.gridx = 1;
        param1Field = new JTextField(20);
        if (media != null) {
            if (media instanceof DocumentMedia) {
                param1Field.setText(String.valueOf(((DocumentMedia) media).getPageCount()));
            } else if (media instanceof VideoSession) {
                param1Field.setText(String.valueOf(((VideoSession) media).getDurationMinutes()));
            } else if (media instanceof OnlineQuiz) {
                param1Field.setText(String.valueOf(((OnlineQuiz) media).getEstimatedDuration()));
            }
        }
        formPanel.add(param1Field, gbc);

        // Paramètre 2
        gbc.gridx = 0; gbc.gridy = 6;
        param2Label = new JLabel("Difficulté:");
        formPanel.add(param2Label, gbc);
        gbc.gridx = 1;
        param2Field = new JTextField(20);
        if (media instanceof OnlineQuiz) {
            param2Field.setText(((OnlineQuiz) media).getDifficultyLevel());
        }
        formPanel.add(param2Field, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descArea = new JTextArea(4, 20);
        if (media != null) descArea.setText(media.getDescription());
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll, gbc);

        // Sujets
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Sujets (Ctrl+Click):"), gbc);
        gbc.gridx = 1;
        subjectsModel = new DefaultListModel<>();
        subjectsList = new JList<>(subjectsModel);
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectsList.setVisibleRowCount(4);
        
        // Si étudiant, filtrer les sujets selon ses spécialités
        if (student != null) {
            // Charger seulement les sujets de l'étudiant
            for (Subject subject : library.getAllSubjects()) {
                if (student.getEnrolledSubjects().contains(subject)) {
                    subjectsModel.addElement(subject);
                }
            }
        } else {
            // Admin: charger tous les sujets
            for (Subject subject : library.getAllSubjects()) {
                subjectsModel.addElement(subject);
            }
        }
        
        // Pré-sélectionner les sujets du média existant
        if (media != null) {
            java.util.List<Subject> selected = new java.util.ArrayList<>();
            for (int i = 0; i < subjectsModel.size(); i++) {
                Subject subject = subjectsModel.get(i);
                if (media.getSubjects().contains(subject)) {
                    selected.add(subject);
                }
            }
            subjectsList.setSelectedIndices(getIndices(selected));
        }
        
        JScrollPane subjectsScroll = new JScrollPane(subjectsList);
        formPanel.add(subjectsScroll, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Sauvegarder");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.addActionListener(e -> saveMedia());
        cancelButton.addActionListener(e -> {
            saved = false;
            dispose();
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Mettre à jour les labels
        updateParamLabels();
        
        // Touche Enter pour sauvegarder
        getRootPane().setDefaultButton(saveButton);
    }

    private void updateParamLabels() {
        String type = (String) typeCombo.getSelectedItem();
        if (type == null) return;
        
        switch (type) {
            case "Document":
                param1Label.setText("Pages*:");
                param2Label.setText("Difficulté:");
                param2Field.setEnabled(false);
                param2Field.setText("");
                break;
            case "Video Session":
                param1Label.setText("Durée (min)*:");
                param2Label.setText("Difficulté:");
                param2Field.setEnabled(false);
                param2Field.setText("");
                break;
            case "Online Quiz":
                param1Label.setText("Durée estimée (min)*:");
                param2Label.setText("Difficulté*:");
                param2Field.setEnabled(true);
                break;
        }
    }

    private int[] getIndices(java.util.List<Subject> selectedSubjects) {
        int[] indices = new int[selectedSubjects.size()];
        for (int i = 0; i < selectedSubjects.size(); i++) {
            Subject subject = selectedSubjects.get(i);
            for (int j = 0; j < subjectsModel.size(); j++) {
                if (subjectsModel.get(j).equals(subject)) {
                    indices[i] = j;
                    break;
                }
            }
        }
        return indices;
    }

    private void saveMedia() {
        try {
            // Validation
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String yearStr = yearField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String param1 = param1Field.getText().trim();
            String param2 = param2Field.getText().trim();
            String description = descArea.getText().trim();
            
            // Validation basique
            if (id.isEmpty() || title.isEmpty() || author.isEmpty() || yearStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires (*).", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int year;
            try {
                year = Integer.parseInt(yearStr);
                if (year < 1900 || year > 2100) {
                    throw new NumberFormatException("Année invalide");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une année valide (1900-2100).", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier les paramètres spécifiques
            if ("Online Quiz".equals(type)) {
                if (param1.isEmpty() || param2.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Pour un quiz, veuillez remplir la durée et la difficulté.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                if (param1.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Veuillez remplir le paramètre spécifique (pages ou durée).", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Vérifier si ID existe déjà (seulement pour nouvel ajout)
            if (originalMedia == null && library.getMediaWithoutIncrement(id) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Un média avec cet ID existe déjà.", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer le média avec la factory
            MediaFactory factory = MediaFactoryRegistry.getInstance().getFactory(type.toLowerCase().split(" ")[0]);
            Media media;
            
            try {
                if ("Online Quiz".equals(type)) {
                    int duration = Integer.parseInt(param1);
                    media = factory.createMedia(id, title, author, year, description, duration, param2);
                } else {
                    int param = Integer.parseInt(param1);
                    media = factory.createMedia(id, title, author, year, description, param);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Les paramètres spécifiques doivent être des nombres.", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la création du média: " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ajouter les sujets
            java.util.List<Subject> selectedSubjects = subjectsList.getSelectedValuesList();
            if (selectedSubjects.isEmpty()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "Aucun sujet sélectionné. Voulez-vous continuer?",
                    "Avertissement", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            } else {
                for (Subject subject : selectedSubjects) {
                    media.addSubject(subject);
                }
            }
            
            // Si modification, supprimer l'ancien média
            if (originalMedia != null) {
                library.removeMedia(originalMedia.getId());
            }
            
            // Ajouter le nouveau média
            library.addMedia(media);
            
            // Sauvegarder
            try {
                library.saveAllDataToXML("universite.xml");
                library.saveToBinary("library_data.dat");
            } catch (Exception e) {
                System.out.println("Erreur de sauvegarde: " + e.getMessage());
            }
            
            saved = true;
            JOptionPane.showMessageDialog(this, 
                "Média " + (originalMedia == null ? "ajouté" : "modifié") + " avec succès!", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}